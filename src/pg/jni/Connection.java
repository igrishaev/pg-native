package pg.jni;

import java.util.List;

public class Connection implements AutoCloseable {

    static {
        Native.loadLib();
    }

    private final Arena arena;
    private final long ptr;
    private boolean isClosed;

    private static final long NULL = 0;

    private Connection(Arena arena, long ptr, boolean isClosed) {
        this.arena = arena;
        this.ptr = ptr;
        this.isClosed = isClosed;
    }

    private static native long PQconnectdb(long conninfo);
    private static native int PQstatus(long conn);
    protected static native String PQerrorMessage(long conn);
    private static native long PQexec(long conn, long cmd);
    private static native long PQexecParams(long conn, long command, int nParams, long paramTypes, long paramValues, long paramLengths, long paramFormats, int resultFormat);
    private static native void PQfinish(long ptr);

    public static Connection make(String connInfo) {
        Arena arena = Arena.make(64000);
        long ptr = PQconnectdb(arena.charPtr(connInfo));
        if (ptr == NULL) {
            throw new RuntimeException("failed to connect");
        }
        int status = PQstatus(ptr);
        if (status != 0) {
            throw new RuntimeException("bad connection status: " + status);
        }
        return new Connection(arena, ptr, false);
    }

    public Result exec(String sql) {
        arena.clear();
        long sqlPtr = arena.charPtr(sql);
        long resPtr = PQexec(ptr, sqlPtr);
        if (resPtr == NULL) {
            throw new RuntimeException("failed to execute");
        }
        int status = Result.PQresultStatus(resPtr);
        String message = PQerrorMessage(ptr);
        if (status != 2) {
            throw PGError.make(status, message);
        }
        return Result.make(resPtr, arena);
    }

    public Result execParams(String sql, List<Object> params) {
        int len = params.size();
        int[] paramTypes = new int[len];
        int[] paramLengths = new int[len];
        byte[][] paramValues = new byte[len][];
        int[] paramFormats = new int[len];

        byte[] value;
        for (int i = 0; i < len; i++) {
            paramTypes[i] = 0;
            value = new byte[] {1, 2, 3, 4, 5};
            paramValues[i] = value;
            paramLengths[i] = value.length;
            paramFormats[i] = 0;
        }

        int resultFormat = 0;

        arena.clear();
        long resPtr = PQexecParams(
                ptr,
                arena.charPtr(sql),
                len,
                arena.intArrayPtr(paramTypes),
                arena.charArrayPtr(paramValues),
                arena.intArrayPtr(paramLengths),
                arena.intArrayPtr(paramFormats),
                resultFormat
        );
        if (resPtr == NULL) {
            throw new RuntimeException("failed to execute");
        }
        return Result.make(resPtr, arena);
    }

    @Override
    public void close() {
        if (!isClosed) {
            isClosed = true;
            PQfinish(ptr);
        }
    }
}