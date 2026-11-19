package pg.jni;

import java.util.Arrays;

public class Main {

    private static final String connStr = "host=localhost port=15432 dbname=test user=test password=test";

    public static void main(String... args) {
        try (Connection conn = Connection.make(connStr);
             Result res = conn.exec("select 1 as one, 'hello' as two")
        ) {
            System.out.println(conn);
            System.out.println(res);
            System.out.println(res.getValue(0, 1));
//            System.out.println(res.rowCount());
//            System.out.println(res.colCount());
//            System.out.println(res.colName(1));
//            System.out.println(res.colName(-1));
//            System.out.println(Arrays.toString(res.colNames()));
//            System.out.println(Arrays.toString(res.colTypes()));
//            System.out.println(res.getValue(0, 1));
        }
    }

}
