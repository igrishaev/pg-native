package pg.jni;

public class Row {
    private final long ptr;
    private final int row;

    private Row(long ptr, int row) {
        this.ptr = ptr;
        this.row = row;
    }

    public static Row make(long ptr, int row) {
        return new Row(ptr, row);
    }

}
