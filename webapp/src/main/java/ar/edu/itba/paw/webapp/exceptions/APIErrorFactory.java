package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.webapp.media_types.ErrorMIME;
import ar.edu.itba.paw.webapp.models.error.APIError;
import ar.edu.itba.paw.webapp.models.error.APISubError;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public abstract class APIErrorFactory {
    public static APIErrorBuilder buildError(Status status) {
        return new APIErrorBuilder(new APIError(
                status.getStatusCode(),
                status.toString()
        ));
    }

    public static APIErrorBuilder buildError(int code) {
        Status status = Status.fromStatusCode(code);
        if (status == null) {
            return new APIErrorBuilder(new APIError(code, ""));
        } else {
            return buildError(status);
        }
    }

    public static APIErrorBuilder buildError(int code, String message) {
        return new APIErrorBuilder(
                new APIError(
                        code,
                        message
                )
        );
    }

    public static class APIErrorBuilder {
        private final APIError apiError;

        private APIErrorBuilder(APIError apiError) {
            this.apiError = apiError;
        }

        public APIErrorBuilder withReason(int code, String message) {
            return this.withReason(new APISubError(code, message));
        }

        public APIErrorBuilder withReason(APISubError subError) {
            this.apiError.addSubError(subError);
            return this;
        }

        public APIErrorBuilder withReason(int code) {
            return this.withReason(code, null);
        }

        public APIError build() {
            return this.apiError;
        }

        public WebApplicationException getError() throws WebApplicationException {
            return new WebApplicationException(
                    Response
                            .status(this.apiError.getCode())
                            .entity(this.apiError)
                            .type(ErrorMIME.ERROR)
                            .build()
            );
        }
    }
}
