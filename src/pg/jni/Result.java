package pg.jni;

import pg.jni.codec.Codecs;
import pg.jni.codec.ICodec;

import java.nio.ByteBuffer;

public class Result implements AutoCloseable {

    private final static long NULL;
    static {
        Native.loadLib();
        NULL = Native.getNull();
    }

    private final long ptr;
    private final long connPtr;
    private boolean isClosed;

    private Result(long ptr, long connPtr, boolean isClosed) {
        this.ptr = ptr;
        this.connPtr = connPtr;
        this.isClosed = isClosed;
    }

    public static Result make(long ptr, long connPtr) {
        if (ptr == NULL) {
            throw Err.error("result has failed (got NULL)");
        }
        return new Result(ptr, connPtr, false);
    }

    private static native int _status(long result);
    public int status() {
        return _status(ptr);
    }

    private static native int _nParams(long result);
    public int nParams() {
        return _nParams(ptr);
    }

    private static native int _paramType(long result, int n);
    public int paramType(int n) {
        return _paramType(ptr, n);
    }

    private static native String _colName(long result, int col);
    public String colName(int col) {
        return _colName(ptr, col);
    }

    private static native int _colType(long result, int col);
    public int colType(int col) {
        return _colType(ptr, col);
    }

    private static native int _colFormat(long result, int col);
    public int colFormat(int col) {
        return _colFormat(ptr, col);
    }

    private static native int _colCount(long result);
    public int colCount() {
        return _colCount(ptr);
    }

    private static native int _rowCount(long result);
    public int rowCount() {
        return _rowCount(ptr);
    }

    private static native int _isNull(long result, int row, int col);
    public int isNull(int row, int col) {
        return _isNull(ptr, row, col);
    }

    public Result checkStatus(int statusOK) {
        int status = status();
        if (status != statusOK) {
            String message = Connection._errorMessage(connPtr);
            throw Err.error("wrong result status, expected: %s, got: %s, error: %s", statusOK, status, message);
        }
        return this;
    }

    private static native String _getValueStr(long result, int row, int col);
    private static native byte[] _getValueBuf(long result, int row, int col);

    public Object getValue(int row, int col) {
        final int isNull = isNull(row, col);
        if (isNull == 1) {
            return null;
        }
        // TODO: cache
        final int format = colFormat(col);
        final int type = colType(col);
        final ICodec codec = Codecs.get(type);
        if (codec == null) {
            throw Err.error("unsupported postgres type: %s", type);
        }
        if (format == 0) {
            String str = _getValueStr(ptr, row, col);
            return codec.decodeTxt(str);
        } else {
            byte[] buf = _getValueBuf(ptr, row, col);
            ByteBuffer bb = ByteBuffer.wrap(buf);
            return codec.decodeBin(bb);
        }
    }

    private static native void _clear(long result);
    @Override
    public void close() {
        if (!isClosed) {
            isClosed = true;
            _clear(ptr);
        }
    }
}
