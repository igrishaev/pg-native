package pg.jni;

import java.util.Arrays;

public class Main {

    private static final String connStr = "host=localhost port=15432 dbname=test user=test password=test";

    public static void main(String... args) {
//        try (Connection conn = Connection.make(connStr);
//             Result res = conn.exec("select true as one, 'hello' as two")
//        ) {
//            System.out.println(conn);
//            System.out.println(res.status());
//            System.out.println(res.getValue(0, 0));
//        }
        try (Connection conn = Connection.make(connStr);
             Statement st = conn.prepare("a1", "select $1::int4 as foo")
        ) {
            System.out.println(conn);
            System.out.println(st);
//            System.out.println(res.getValue(0, 0));
        }
    }

}
