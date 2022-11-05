package ar.edu.itba.paw.webapp.models.error;

import java.util.Collection;
import java.util.LinkedList;

public class APIError extends APIBaseError {
    private final Collection<APISubError> subErrors;

    public APIError(int code, String message) {
        this(code, message, new LinkedList<>());
    }

    public APIError(int code, String message, Collection<APISubError> subErrors) {
        super(code, message);
        this.subErrors = subErrors;
    }

    public Collection<APISubError> getSubErrors() {
        return this.subErrors;
    }

    public void addSubError(APISubError subError) {
        this.subErrors.add(subError);
    }
}
