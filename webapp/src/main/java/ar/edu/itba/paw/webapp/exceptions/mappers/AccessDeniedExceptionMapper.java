package ar.edu.itba.paw.webapp.exceptions.mappers;

import ar.edu.itba.paw.webapp.exceptions.APIErrorFactory;
import ar.edu.itba.paw.webapp.media_types.ErrorMIME;
import ar.edu.itba.paw.webapp.models.error.ErrorConstants;
import org.springframework.security.access.AccessDeniedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AccessDeniedExceptionMapper implements ExceptionMapper<AccessDeniedException> {
    @Override
    public Response toResponse(AccessDeniedException exception) {
        return Response
                .status(Status.FORBIDDEN)
                .entity(
                        APIErrorFactory
                                .buildError(Status.FORBIDDEN)
                                .withReason(ErrorConstants.FORBIDDEN)
                                .build()
                )
                .type(ErrorMIME.ERROR)
                .build();
    }
}