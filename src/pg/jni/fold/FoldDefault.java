package pg.jni.fold;

import pg.jni.Row;

import java.util.ArrayList;
import java.util.HashMap;

public class FoldDefault implements IFolder<FoldDefault.Acc, FoldDefault.Acc> {

    public static class Item extends HashMap<String, Object> {}
    public static class Acc extends ArrayList<Item> {}

    public static final FoldDefault INSTANCE = new FoldDefault();

    @Override
    public Acc init() {
        return new Acc();
    }

    @Override
    public Acc append(Acc acc, Row row) {
        Item map = new Item();
        String column;
        Object value;
        String[] colNames = row.colNames();
        for (int i = 0; i < colNames.length; i++) {
            column = colNames[i];
            value = row.getValue(i);
            map.put(column, value);
        }
        acc.add(map);
        return acc;
    }

    @Override
    public Acc finalize(Acc acc) {
        return acc;
    }
}
