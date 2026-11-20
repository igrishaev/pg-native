package pg.jni.enums;

// https://github.com/postgres/postgres/blob/master/src/interfaces/libpq/libpq-fe.h#L128
public class PGRES {
    public static final int EMPTY_QUERY = 0;
    public static final int COMMAND_OK = 1;
    public static final int TUPLES_OK = 2;
    public static final int COPY_OUT = 3;
    public static final int COPY_IN = 4;
    public static final int BAD_RESPONSE = 5;
    public static final int NONFATAL_ERROR = 6;
    public static final int FATAL_ERROR = 7;
    public static final int COPY_BOTH = 8;
    public static final int SINGLE_TUPLE = 9;
    public static final int PIPELINE_SYNC = 10;
    public static final int PIPELINE_ABORTED = 11;
    public static final int TUPLES_CHUNK = 12;

}
