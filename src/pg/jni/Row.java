package pg.jni;

public class Row {

    private final int row;
    private final String[] colNames;
    private final int[] colTypes;
    private final int[] colFormats;

    public Row(int row, String[] colNames, int[] colTypes, int[] colFormats) {
        this.row = row;
        this.colNames = colNames;
        this.colTypes = colTypes;
        this.colFormats = colFormats;
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
