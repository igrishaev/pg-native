package pg.jni;

public class PGError extends RuntimeException {

    private final int errorStatus;
    private final String errorMessage;

    private PGError(int errorStatus, String errorMessage) {
        this.errorStatus = errorStatus;
        this.errorMessage = errorMessage;
    }

    public static PGError make(int errorStatus, String errorMessage) {
        return new PGError(errorStatus, errorMessage);
    }

    @Override
    public String toString() {
        return String.format("<PGError, status: %s, message: %s>", errorStatus, errorMessage);
    }
}
