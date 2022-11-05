package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.webapp.exceptions.APIErrorFactory;
import ar.edu.itba.paw.webapp.exceptions.ExceptionResponseWriter;
import ar.edu.itba.paw.webapp.models.Constants;
import ar.edu.itba.paw.webapp.models.error.APIError;
import ar.edu.itba.paw.webapp.models.error.ErrorConstants;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;

public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        // If the user is trying to refresh a JWT token then we should return Forbidden always
        if (httpServletRequest.getPathInfo().equalsIgnoreCase("/" + Constants.AUTH_ENDPOINT + "/" + Constants.REFRESH_TOKEN_ENDPOINT)) {
            ExceptionResponseWriter.setError(
                    httpServletResponse,
                    APIErrorFactory
                        .buildError(Status.FORBIDDEN)
                        .withReason(ErrorConstants.MISSING_INVALID_REFRESH_TOKEN)
                        .build()
            );
        } else {
            // We need to see if the JWT cookie was valid
            // If it was, then we need to throw a Forbidden error
            // It the user wasn't logged in, then we need to throw an Unauthorized error.
            boolean validJWT = (boolean) httpServletRequest.getAttribute(Constants.VALID_JWT_REQUEST_ATTRIBUTE);
            APIError apiError;
            if (!validJWT) {
                apiError = APIErrorFactory
                        .buildError(Status.UNAUTHORIZED)
                        .withReason(ErrorConstants.UNAUTHORIZED)
                        .build();
            } else {
                apiError = APIErrorFactory
                        .buildError(Status.FORBIDDEN)
                        .withReason(ErrorConstants.FORBIDDEN)
                        .build();
            }

            ExceptionResponseWriter.setError(httpServletResponse, apiError);
        }
    }
}
