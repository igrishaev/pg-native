package pg.jni;

import pg.jni.codec.Codecs;
import pg.jni.codec.ICodec;

import java.nio.ByteBuffer;
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

    private native static long _execPrepared(long conn, long ptrName, int nParams, long ptrValues, long ptrLengths, long ptrFormats, int resultFormat);

    public Result execPrepared(Statement st, List<Object> params) {
        int nParams = st.nParams();
        if (nParams != params.size()) {
            throw new RuntimeException("wrong params count");
        }
        int resultFormat = 1;
        int[] oids = st.oids();
        byte[][] values = new byte[nParams][];
        int[] lengths = new int[nParams];
        int[] formats = new int[nParams];
        int format;
        Object param;
        ICodec codec;
        byte[] buf;
        int len;
        int oid;
        for (int i = 0; i < nParams; i++) {
            format = 1;
            param = params.get(i); // TODO check
            oid = oids[i];
            codec = Codecs.get(oid);
            ByteBuffer bb = codec.encodeBin(param);
            buf = bb.array();
            len = buf.length;
            formats[i] = format;
            lengths[i] = len;
            values[i] = buf;
        }
        arena.clear();
        long ptrName = arena.charPtr(st.name());
        long ptrValues = arena.charArrayPtr(values);
        long ptrLengths = arena.intArrayPtr(lengths);
        long ptrFormats = arena.intArrayPtr(formats);
        long resPtr = _execPrepared(
                ptr,
                ptrName,
                nParams,
                ptrValues,
                ptrLengths,
                ptrFormats,
                resultFormat
        );
        if (resPtr == NULL) {
            throw new RuntimeException("failed to _execPrepared");
        }
        return Result.make(resPtr);
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