package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Doctor;
import ar.edu.itba.paw.webapp.exceptions.APIErrorFactory;
import ar.edu.itba.paw.webapp.exceptions.ExceptionResponseWriter;
import ar.edu.itba.paw.webapp.media_types.LoginMIME;
import ar.edu.itba.paw.webapp.media_types.MIMEHelper;
import ar.edu.itba.paw.webapp.media_types.UserMIME;
import ar.edu.itba.paw.webapp.media_types.parsers.serializers.UserMeSerializer;
import ar.edu.itba.paw.webapp.models.Constants;
import ar.edu.itba.paw.webapp.models.UserCredentials;
import ar.edu.itba.paw.webapp.models.UserMe;
import ar.edu.itba.paw.webapp.models.UserMeFactory;
import ar.edu.itba.paw.webapp.models.error.ErrorConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@Component
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    @Autowired
    private UserService userService;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private JWTAuthenticator authenticator;

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    @Value("classpath:token.key")
    private Resource secretResource;

    public JWTAuthenticationFilter() throws IOException {
        this.setFilterProcessesUrl("/api/login");
    }

    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!HttpMethod.POST.equalsIgnoreCase(request.getMethod())) {
            ExceptionResponseWriter.setError(response, Status.METHOD_NOT_ALLOWED);
            return null;
        }
        try {
            MIMEHelper.assertServerType(request);
            MIMEHelper.assertClientType(request, LoginMIME.POST);
        } catch (NotAcceptableException e) {
            ExceptionResponseWriter.setError(response, Status.NOT_ACCEPTABLE);
            return null;
        }

        if (((boolean) request.getAttribute(Constants.VALID_JWT_REQUEST_ATTRIBUTE))) {
            // The user has already logged in
            ExceptionResponseWriter.setError(
                    response,
                    APIErrorFactory
                            .buildError(Status.FORBIDDEN)
                            .withReason(ErrorConstants.FORBIDDEN)
                            .build()
            );
            return null;
        }

        UserCredentials credentials;
        try {
            credentials = new ObjectMapper().readValue(request.getInputStream(), UserCredentials.class);
        } catch (UnrecognizedPropertyException e) {
            ExceptionResponseWriter.setError(response, Status.BAD_REQUEST);
            return null;
        } catch (IOException e) {
            ExceptionResponseWriter.setError(
                    response,
                    APIErrorFactory
                            .buildError(Status.INTERNAL_SERVER_ERROR)
                            .withReason(ErrorConstants.INTERNAL_SERVER_ERROR)
                            .build()
            );
            return null;
        }

        try {
            return this.authenticator.attemptAuthentication(credentials);
        } catch (AuthenticationException e) {
            ExceptionResponseWriter.setError(
                    response,
                    APIErrorFactory
                            .buildError(Status.UNAUTHORIZED)
                            .withReason(ErrorConstants.LOGIN_INVALID_CREDENTIALS)
                            .build()
            );
            return null;
        } catch (Exception e) {
            ExceptionResponseWriter.setError(
                    response,
                    APIErrorFactory
                            .buildError(Status.INTERNAL_SERVER_ERROR)
                            .withReason(ErrorConstants.INTERNAL_SERVER_ERROR)
                            .build()
            );
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        ar.edu.itba.paw.models.User user;
        Optional<ar.edu.itba.paw.models.User> maybeUser;
        try {
            maybeUser = this.userService.findById(Integer.parseInt(((User) authentication.getPrincipal()).getUsername()));
        } catch (NumberFormatException e) {
            LOGGER.error("Bad username, should be a number. Username: {}", ((User) authentication.getPrincipal()).getUsername());
            throw new IllegalStateException("Could not authenticate because of Id");
        }
        if(!maybeUser.isPresent()){
            throw new IllegalStateException("Could not authenticate because user not found");
        } else {
            user = maybeUser.get();
        }

        this.authenticator.createAndRefreshJWT(authentication, user, response, null);

        UserMe userMe;
        Collection<Doctor> doctors = this.doctorService.findByUser(user);
        if (doctors.size() == 0) {
            userMe = UserMeFactory.withPatients(user, this.patientService.findByUser(user));
        } else {
            userMe = UserMeFactory.withDoctors(user, doctors);
        }

        response.setStatus(Status.OK.getStatusCode());
        response.setHeader(HttpHeaders.CONTENT_TYPE, UserMIME.ME);
        response.getWriter().append(UserMeSerializer.instance.toJson(userMe).toString());
    }
}