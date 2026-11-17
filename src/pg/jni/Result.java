package pg.jni;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class Result implements AutoCloseable, Iterable<Row> {

    static {
        Native.loadLib();
    }

    private final long ptr;
    private boolean isClosed;
    private final Arena arena;
    private final int rowCount;
    private final int nFields;
    private final String[] colNames;
    private final int[] colFormats;
    private final int[] colTypes;

    private Result(long ptr, Arena arena, boolean isClosed, int rowCount, int nFields, String[] colNames, int[] colFormats, int[] colTypes) {
        this.ptr = ptr;
        this.arena = arena;
        this.isClosed = isClosed;
        this.rowCount = rowCount;
        this.nFields = nFields;
        this.colNames = colNames;
        this.colFormats = colFormats;
        this.colTypes = colTypes;
    }

    public static Result make(long ptr, Arena arena) {
        int res = getResultInfo(ptr, arena.ptr);
        if (res != 0) {
            throw new RuntimeException("failed to get result info");
        }
        arena.clear();
        int nTuples = arena.getInt();
        int nFields = arena.getInt();

        String[] colNames = new String[nFields];
        int[] colFormats = new int[nFields];
        int[] colTypes = new int[nFields];

        for (int i = 0; i < nFields; i++) {
            colNames[i] = arena.getCString();
            colFormats[i] = arena.getInt();
            colTypes[i] = arena.getInt();
        }

        return new Result(ptr, arena, false, nTuples, nFields, colNames, colFormats, colTypes);
    }

    private static native int getResultInfo(long ptr, long bb);
    protected static native int PQresultStatus(long result);
    private static native int getFieldInfo(long ptr, int row, int col, long bb);

    public String getValue(int row, int col) {
        if (row >= rowCount) {
            throw Err.error("wrong row: %s", row);
        }
        if (col >= nFields) {
            throw Err.error("wrong column: %s", col);
        }
        int res = getFieldInfo(ptr, row, col, arena.ptr);
        if (res != 0) {
            throw new RuntimeException("failed to get row/col value");
        }
        arena.clear();
        int isNull = arena.getInt();
        if (isNull == 1) {
            return null;
        }

        int colType = arena.getInt();
        int colFormat = arena.getInt();
        int size = arena.getInt();

        if (colFormat == 1) {
            ByteBuffer bb = arena.slice(size);
            bb.order(ByteOrder.BIG_ENDIAN);
            return null;
        } else {
            byte[] buf = new byte[size];
            arena.get(buf);
            return new String(buf, StandardCharsets.UTF_8);
        }
    }

    @Override
    public Iterator<Row> iterator() {
        return new Iterator<>() {
            private int row = 0;
            @Override
            public boolean hasNext() {
                return row < rowCount;
            }
            @Override
            public Row next() {
                return Row.make(ptr, row++);
            }
        };
    }

    private static native void PQclear(long ptr);

    @Override
    public void close() {
        if (!isClosed) {
            isClosed = true;
            PQclear(ptr);
        }
    }
}
