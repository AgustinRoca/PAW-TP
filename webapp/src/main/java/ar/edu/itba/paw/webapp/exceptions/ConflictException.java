package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.webapp.exceptions.APIErrorFactory.APIErrorBuilder;

import javax.ws.rs.core.Response.Status;

public abstract class ConflictException {
    public static APIErrorBuilder build() {
        return APIErrorFactory.buildError(Status.CONFLICT);
    }
}
