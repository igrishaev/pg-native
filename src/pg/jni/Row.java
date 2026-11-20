package pg.jni;

public class Row {

    private int row;
    private final String[] colNames;
    private final int[] colTypes;
    private final int[] colFormats;
    private final long resPtr;

    public Row(int row, String[] colNames, int[] colTypes, int[] colFormats, long resPtr) {
        this.row = row;
        this.colNames = colNames;
        this.colTypes = colTypes;
        this.colFormats = colFormats;
        this.resPtr = resPtr;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String[] colNames() {
        return colNames;
    }

    public Object getValue(int col) {
        return null;
    }

    public Object getValue(String col) {
        return null;
    }
}
