package pg.jni;

import pg.jni.codec.Codecs;
import pg.jni.codec.ICodec;

import java.nio.ByteBuffer;

public class Result implements AutoCloseable {

    static {
        Native.loadLib();
    }

    private final long ptr;
    private boolean isClosed;

    private Result(long ptr, boolean isClosed) {
        this.ptr = ptr;
        this.isClosed = isClosed;
    }

    public static Result make(long ptr, Arena arena) {
        return new Result(ptr, false);
    }

    protected static native int PQresultStatus(long result);
    private static native void PQclear(long result);

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

    @Override
    public void close() {
        if (!isClosed) {
            isClosed = true;
            PQclear(ptr);
        }
    }
}
