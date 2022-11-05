package ar.edu.itba.paw.webapp.models.error;

public abstract class APIBaseError {
    private final int code;
    private final String message;

    public APIBaseError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}

