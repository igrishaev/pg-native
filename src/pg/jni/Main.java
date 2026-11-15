package pg.jni;

public class Main {

    private static final String connStr = "host=localhost port=15432 dbname=test user=test password=test";

    public static void main(String... args) {
        try (Connection conn = Connection.make(connStr)) {
            System.out.println(conn);
        }
    }

}
