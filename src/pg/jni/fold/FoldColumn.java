package pg.jni.fold;

import pg.jni.Row;

import java.util.ArrayList;

public class FoldColumn implements IFolder<FoldColumn.Acc, FoldColumn.Acc> {

    public static class Acc extends ArrayList<Object> {}

    private final String column;

    public FoldColumn(String column) {
        this.column = column;
    }

    @Override
    public Acc init() {
        return new Acc();
    }

    @Override
    public Acc append(Acc acc, Row row) {
        acc.add(row.getValue(column));
        return acc;
    }

    @Override
    public Acc finalize(Acc acc) {
        return acc;
    }
}
