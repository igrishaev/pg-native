package pg.jni;

import java.util.HashMap;
import java.util.Map;

public class Row {

    private int row;
    private final String[] colNames;
    private final int[] colTypes;
    private final int[] colFormats;
    private final Result result;
    private final Map<String, Integer> colIndex;

    private Row(
            int index, String[] colNames, int[] colTypes, int[] colFormats,
            Map<String, Integer> colIndex, Result result
    ) {
        this.row = index;
        this.colNames = colNames;
        this.colTypes = colTypes;
        this.colFormats = colFormats;
        this.colIndex = colIndex;
        this.result = result;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public static Row make(Result result) {
        int colCount = result.colCount();

        // TODO: batch methods
        String[] colNames = new String[colCount];
        int[] colTypes = new int[colCount];
        int[] colFormats = new int[colCount];
        Map<String, Integer> colIndex = new HashMap<>(colCount);

        String colName;
        for (int i = 0; i < colCount; i++) {
            colName = result.colName(i);
            colNames[i] = colName;
            colTypes[i] = result.colType(i);
            colFormats[i] = result.colFormat(i);
            colIndex.put(colName, i);
        }

        return new Row(-1, colNames, colTypes, colFormats, colIndex, result);
    }

    public String[] colNames() {
        return colNames;
    }

    public Object getValue(int col) {
        int type = colTypes[col];
        int format = colFormats[col];
        return result.getValue(row, col, type, format);
    }

    public Object getValue(String colName) {
        int col = colIndex.get(colName);
        return getValue(col);
    }
}
