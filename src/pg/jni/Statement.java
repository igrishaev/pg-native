package pg.jni;

public class Statement implements AutoCloseable {

    static {
        Native.loadLib();
    }

    private final long connPtr;
    private final String name;
    private final String sql;
    private final int nParams;
    private final int[] oids;
    private final Arena arena;
    private boolean isClosed;

    public Statement(long connPtr, String name, String sql, int nParams, int[] oids, Arena arena) {
        this.connPtr = connPtr;
        this.name = name;
        this.sql = sql;
        this.nParams = nParams;
        this.oids = oids;
        this.arena = arena;
        this.isClosed = false;
    }

    public String name() {
        return name;
    }

    public int nParams() {
        return nParams;
    }

    public int[] oids() {
        return oids;
    }

    private static native long _closePrepared(long connPtr, long ptrName);

    @Override
    public void close() {
        if (isClosed) {
            return;
        }
        isClosed = true;
        arena.clear();
        long namePtr = arena.charPtr(name);
        long resPtr = _closePrepared(connPtr, namePtr);
        if (resPtr == Arena.NULL) {
            throw new RuntimeException("aaa");
        }
        try (Result r = Result.make(resPtr)) {
            System.out.println(r.status());
        }
    }
}
