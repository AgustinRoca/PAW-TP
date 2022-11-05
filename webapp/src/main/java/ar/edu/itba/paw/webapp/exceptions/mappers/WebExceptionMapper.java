package ar.edu.itba.paw.webapp.exceptions.mappers;

import ar.edu.itba.paw.webapp.exceptions.APIErrorFactory;
import ar.edu.itba.paw.webapp.media_types.ErrorMIME;
import ar.edu.itba.paw.webapp.models.error.APIError;
import ar.edu.itba.paw.webapp.models.error.ErrorConstants;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WebExceptionMapper implements ExceptionMapper<WebApplicationException> {
    @Override
    public Response toResponse(WebApplicationException exception) {
        if (exception.getResponse() != null) {
            Object entity;
            String mediaType;

            if (!(exception.getResponse().getEntity() instanceof APIError))
                entity = APIErrorFactory.buildError(exception.getResponse().getStatus()).build();
            else
                entity = exception.getResponse().getEntity();

            mediaType = ErrorMIME.ERROR;

            return Response
                    .status(exception.getResponse().getStatus())
                    .entity(entity)
                    .type(mediaType)
                    .build();
        } else {
            return Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity(
                            APIErrorFactory
                                    .buildError(Status.INTERNAL_SERVER_ERROR)
                                    .withReason(ErrorConstants.INTERNAL_SERVER_ERROR)
                                    .build()
                    )
                    .type(ErrorMIME.ERROR)
                    .build();
        }
    }
}