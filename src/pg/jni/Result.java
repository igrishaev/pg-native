package pg.jni;

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
        if (res == 0) {
            throw new RuntimeException("failed to get result info");
        }
        arena.clear().boNative();
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
    private static native int getFieldInfo(long ptr, int row, int col);

    public String getValue(int row, int col) {
        arena.boNative();
        int res = getFieldInfo(ptr, row, col);
        if (res == 0) {
            throw new RuntimeException("failed to get row/col value");
        }
        int oid = arena.getInt();
        int format = arena.getInt();
        int isNull = arena.getInt();
        int len = arena.getInt();
        byte[] value = new byte[len];
        arena.get(value);
        return new String(value);
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
