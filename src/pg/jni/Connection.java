package pg.jni;

import java.util.List;

public class Connection implements AutoCloseable {

    private static native long getNull();

    private static final long NULL;
    static {
        Native.loadLib();
        NULL = getNull();
    }

    private final Arena arena;
    private final long ptr;
    private boolean isClosed;


    private Connection(Arena arena, long ptr, boolean isClosed) {
        this.arena = arena;
        this.ptr = ptr;
        this.isClosed = isClosed;
    }

    private static native long PQconnectdb(long conninfo);
    private static native int PQstatus(long conn);
    protected static native String PQerrorMessage(long conn);
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

//    public void checkResult(long resPtr, int statusOK) {
//        int status = Result.PQresultStatus(resPtr);
//        if (status != statusOK) {
//            String message = PQerrorMessage(ptr);
//            throw PGError.make(status, message);
//        }
//    }

    private static native long _exec(long conn, long cmd);
    public Result exec(String sql) {
        arena.clear();
        long sqlPtr = arena.charPtr(sql);
        long resPtr = _exec(ptr, sqlPtr);
        if (resPtr == NULL) {
            throw new RuntimeException("failed to execute");
        }
        return Result.make(resPtr);
    }

    private native static long _prepare(long conn, long ptrName, long ptrSql, int nParams, long ptrOids);
    private native static long _describePrepared(long conn, long ptrName);

    public Statement prepare(String name, String sql) {

        long resPtr;

        arena.clear();
        long ptrName = arena.charPtr(name);
        long ptrSql = arena.charPtr(sql);

        resPtr = _prepare(ptr, ptrName, ptrSql, 0, NULL);
        if (resPtr == NULL) {
            throw new RuntimeException("failed to _prepare");
        }
        try (Result r = Result.make(resPtr)) {
            System.out.println(r.status());
        }

        resPtr = _describePrepared(ptr, ptrName);
        if (resPtr == NULL) {
            throw new RuntimeException("failed to _describePrepared");
        }
        int nParams;
        int[] oids;
        try (Result r = Result.make(resPtr)) {
            System.out.println(r.status());
            nParams = r.nParams();
            oids = new int[nParams];
            for (int i = 0; i < nParams; i++) {
                oids[i] = r.paramType(i);
            }
        }

        return new Statement(ptr, name, sql, nParams, oids, arena);
    }

    public Result execPrepared(Statement st, List<Object> params) {
        return null;
    }



//    public Result execParams(String sql, List<Object> params) {
//        int len = params.size();
//        int[] paramTypes = new int[len];
//        int[] paramLengths = new int[len];
//        byte[][] paramValues = new byte[len][];
//        int[] paramFormats = new int[len];
//
//        byte[] value;
//        for (int i = 0; i < len; i++) {
//            paramTypes[i] = 0;
//            value = new byte[] {1, 2, 3, 4, 5};
//            paramValues[i] = value;
//            paramLengths[i] = value.length;
//            paramFormats[i] = 0;
//        }
//
//        int resultFormat = 0;
//
//        arena.clear();
//        long resPtr = PQexecParams(
//                ptr,
//                arena.charPtr(sql),
//                len,
//                arena.intArrayPtr(paramTypes),
//                arena.charArrayPtr(paramValues),
//                arena.intArrayPtr(paramLengths),
//                arena.intArrayPtr(paramFormats),
//                resultFormat
//        );
//        if (resPtr == NULL) {
//            throw new RuntimeException("failed to execute");
//        }
//        return Result.make(resPtr, arena);
//    }

    @Override
    public void close() {
        if (!isClosed) {
            isClosed = true;
            PQfinish(ptr);
        }
    }
}