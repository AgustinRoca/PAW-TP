package ar.edu.itba.paw.webapp.rest.utils;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.JWTAuthenticator;
import ar.edu.itba.paw.webapp.auth.UserRole;
import ar.edu.itba.paw.webapp.models.Constants;
import ar.edu.itba.paw.webapp.models.UserCredentials;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public abstract class GenericAuthenticationResource extends GenericResource {
    @Autowired
    protected JWTAuthenticator authenticator;
    @Autowired
    private UserService userService;

    protected String getRefreshTokenString(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(Constants.REFRESH_TOKEN_COOKIE_NAME))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    protected boolean createJWTCookies(UserCredentials credentials, User user, HttpServletResponse response, String refreshToken, Logger logger) {
        Collection<? extends GrantedAuthority> authorities;
        if (!user.getVerified()) {
            authorities = Collections.singletonList(new SimpleGrantedAuthority(UserRole.UNVERIFIED.getAsRole()));
        } else {
            if (this.userService.isDoctor(user)) {
                authorities = Collections.singletonList(new SimpleGrantedAuthority(UserRole.DOCTOR.getAsRole()));
            } else {
                authorities = Collections.singletonList(new SimpleGrantedAuthority(UserRole.PATIENT.getAsRole()));
            }
        }

        try {
            Authentication authentication = this.authenticator.createAuthentication(credentials, authorities);
            this.authenticator.createAndRefreshJWT(authentication, user, response, refreshToken);
        } catch (Exception e) {
            logger.error("Error creating JWT token for user id: {} with mail: {}", user.getId(), user.getEmail());
            return false;
        }

        return true;
    }
}
