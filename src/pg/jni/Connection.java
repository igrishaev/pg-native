package pg.jni;

import pg.jni.codec.Codecs;
import pg.jni.codec.ICodec;
import pg.jni.enums.CONNECTION;
import pg.jni.enums.PGRES;

import java.nio.ByteBuffer;
import java.util.List;

public class Connection implements AutoCloseable {

    private final static long NULL;
    static {
        Native.loadLib();
        NULL = Native.getNull();
    }

    private final Arena arena;
    private final long ptr;
    private boolean isClosed;

    private Connection(long ptr, Arena arena, boolean isClosed) {
        this.ptr = ptr;
        this.arena = arena;
        this.isClosed = isClosed;
    }

    private static native long _connect(long connStringPtr);

    private static native int _status(long connPtr);
    public int status() {
        return _status(ptr);
    }

    protected static native String _errorMessage(long connPtr);
    public String errorMessage() {
        return _errorMessage(ptr);
    }

    public static Connection make(String connInfo) {
        Arena arena = Arena.make(Const.ARENA_SIZE);
        long ptr = _connect(arena.charPtr(connInfo));
        if (ptr == NULL) {
            throw Err.error("connection failure (NULL result)");
        }
        Connection conn = new Connection(ptr, arena, false);
        int status = conn.status();
        if (status != CONNECTION.OK) {
            String message = conn.errorMessage();
            conn.close();
            throw Err.error("bad connection status: %s, message: %s", status, message);
        }
        return conn;
    }

    public static native void _reset(long connPtr);
    public void reset() {
        _reset(ptr);
    }

    private static native long _exec(long conn, long cmd);
    public Result exec(String sql) {
        arena.clear();
        long sqlPtr = arena.charPtr(sql);
        long resPtr = _exec(ptr, sqlPtr);
        if (resPtr == NULL) {
            throw Err.error("exec failure (NULL result), conn: %s, sql: %s", this, sql);
        }
        // TODO: check update
        return Result.make(resPtr, ptr).checkStatus(PGRES.TUPLES_OK);
    }

    private native static long _prepare(long conn, long ptrName, long ptrSql, int nParams, long ptrOids);
    private native static long _describePrepared(long conn, long ptrName);

    public Statement prepare(String name, String sql) {
        long resPtr;
        arena.clear();
        long ptrName = arena.charPtr(name);
        long ptrSql = arena.charPtr(sql);

        resPtr = _prepare(ptr, ptrName, ptrSql, 0, NULL);
        try (Result r = Result.make(resPtr, ptr)) {
            r.checkStatus(PGRES.COMMAND_OK);
        }

        int nParams;
        int[] oids;
        resPtr = _describePrepared(ptr, ptrName);
        try (Result r = Result.make(resPtr, ptr)) {
            r.checkStatus(PGRES.COMMAND_OK);
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
        int nParamsPassed = params.size();
        if (nParams != nParamsPassed) {
            throw Err.error("wrong parameters count, expected %s, passed %s", nParams, nParamsPassed);
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
        // TODO: check update
        return Result.make(resPtr, ptr).checkStatus(PGRES.TUPLES_OK);
    }

    private static native void _clear(long connPtr);

    @Override
    public void close() {
        if (!isClosed) {
            isClosed = true;
            _clear(ptr);
        }
    }
}
