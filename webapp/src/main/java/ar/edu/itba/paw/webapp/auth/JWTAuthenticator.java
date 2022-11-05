package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.RefreshTokenService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.webapp.media_types.parsers.serializers.UserJWTSerializer;
import ar.edu.itba.paw.webapp.models.Constants;
import ar.edu.itba.paw.webapp.models.UserCredentials;
import ar.edu.itba.paw.webapp.utils.CookieUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@PropertySource("classpath:application-prod.properties")
@PropertySource(value = "classpath:application-local.properties", ignoreResourceNotFound = true) // This will precede previous properties
public class JWTAuthenticator {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private UserService userService;

    @Value("${app.api.path}")
    private String APP_SUBPATH;
    @Value("${app.cookie.host}")
    private String APP_HOST;

    private final String secret;

    public JWTAuthenticator() throws IOException {
        this.secret = this.getSecretKey();
    }

    public Authentication attemptAuthentication(UserCredentials credentials) throws AuthenticationException {
        return this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentials.getUsername(),
                        credentials.getPassword(),
                        new LinkedList<>()
                ));
    }

    public Authentication createAuthentication(UserCredentials credentials, Collection<? extends GrantedAuthority> authorities) throws AuthenticationException {
        try {
            return new UsernamePasswordAuthenticationToken(
                            credentials.getUsername(),
                            credentials.getPassword(),
                            authorities
           );
        } catch (AuthenticationException e) {
            return null;
        }
    }

    /**
     * @param authentication
     * @param user
     * @return headers
     */
    public void createAndRefreshJWT(Authentication authentication, ar.edu.itba.paw.models.User user, HttpServletResponse response, String refreshToken) {
        Map<String, Object> claims = new HashMap<>();

        claims.put(Constants.JWT_CLAIMS_DATA, UserJWTSerializer.instance.toJson(user).toString());
        Optional<? extends GrantedAuthority> authority = authentication.getAuthorities().stream().findFirst();
        authority.ifPresent(grantedAuthority -> claims.put(Constants.JWT_CLAIMS_ROLE, grantedAuthority.getAuthority()));

        String token = Jwts.builder()
                .setIssuedAt(new Date())
                .setIssuer(this.APP_HOST)
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + Constants.JWT_EXPIRATION_MILLIS))
                .signWith(SignatureAlgorithm.HS512, this.secret)
                .compact();

        if (refreshToken != null) {
            refreshToken = this.refreshTokenService.refresh(refreshToken);
        } else {
            refreshToken = this.refreshTokenService.generate(user);
        }

        CookieUtils.setHttpOnlyCookie(response, this.createJWTCookie(token));
        CookieUtils.setHttpOnlyCookie(response, this.createRefreshTokenCookie(refreshToken));
        this.createJWTExpirationHeader(response);
    }

    public void invalidateJWTCookies(HttpServletResponse response) {
        CookieUtils.setHttpOnlyCookie(response, this.createJWTCookie(Constants.EMPTY_COOKIE));
        CookieUtils.setHttpOnlyCookie(response, this.createRefreshTokenCookie(Constants.EMPTY_COOKIE));
        this.createJWTExpirationHeader(response);
    }

    private void createJWTExpirationHeader(HttpServletResponse response) {
        response.setHeader(Constants.LOGGED_IN_TTL_HEADER_NAME, Long.toString(Constants.JWT_EXPIRATION_MILLIS));
        response.setHeader(Constants.REFRESH_TOKEN_TTL_HEADER_NAME, Long.toString(Constants.JWT_REFRESH_EXPIRATION_MILLIS));
    }

    private Cookie createJWTCookie(String token) {
        Cookie cookie = new Cookie(Constants.JWT_COOKIE_NAME, token);
        // No usamos secure porque paw no tiene ssl
        // jwtCookie.setSecure(true);

        if (!token.isEmpty())
            cookie.setMaxAge((int) TimeUnit.MILLISECONDS.toSeconds(Constants.JWT_EXPIRATION_MILLIS));
        cookie.setDomain(this.APP_HOST);
        cookie.setPath(this.APP_SUBPATH);

        return cookie;
    }

    private Cookie createRefreshTokenCookie(String token) {
        Cookie cookie = new Cookie(Constants.REFRESH_TOKEN_COOKIE_NAME, token);
        // No usamos secure porque paw no tiene ssl
        // jwtCookie.setSecure(true);

        if (!token.isEmpty())
            cookie.setMaxAge((int) TimeUnit.MILLISECONDS.toSeconds(Constants.JWT_REFRESH_EXPIRATION_MILLIS));
        cookie.setDomain(this.APP_HOST);
        cookie.setPath(this.APP_SUBPATH + '/' + Constants.AUTH_ENDPOINT);

        return cookie;
    }

    private String getSecretKey() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("token.key");
        Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
        return FileCopyUtils.copyToString(reader);
    }
}
