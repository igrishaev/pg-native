package pg.jni;

import java.util.List;
import java.util.Objects;

public class Connection implements AutoCloseable {

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

    public static Connection make(String connInfo) {
        Arena arena = Arena.make(64000);
        long ptr = PQconnectdb(arena.charPtr(connInfo));
        if (ptr == NULL) {
            throw new RuntimeException("failed to connect");
        }
        return new Connection(arena, ptr, false);
    }

    private static native long PQexecParams(long conn, long command, int nParams, long paramTypes, long paramValues, long paramLengths, long paramFormats, int resultFormat);

    public Result execParams(String sql, List<Objects> params) {
        int len = params.size();
        int[] paramTypes = new int[len];
        int[] paramLengths = new int[len];
        byte[][] paramValues = new byte[len][];
        int[] paramFormats = new int[len];

        byte[] value;
        for (int i = 0; i < len; i++) {
            paramTypes[i] = 1;
            value = new byte[] {1, 2, 3, 4, 5};
            paramValues[i] = value;
            paramLengths[i] = value.length;
            paramFormats[i] = 0;
        }

        arena.clear().boNative();

        long resPtr = PQexecParams(
                ptr,
                arena.charPtr(sql),
                len,
                arena.intArrayPtr(paramTypes),
                arena.byteArray2Ptr(paramValues),
                arena.intArrayPtr(paramLengths),
                arena.intArrayPtr(paramFormats),
                0
        );
        if (resPtr == NULL) {
            throw new RuntimeException("failed to execute");
        }
        return Result.make(resPtr, arena);
    }

    private static native void PQfinish(long ptr);

    @Override
    public void close() {
        if (!isClosed) {
            isClosed = true;
            PQfinish(ptr);
        }
    }
}