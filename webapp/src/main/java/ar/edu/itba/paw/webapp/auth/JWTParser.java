package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.media_types.parsers.deserializers.UserJWTDeserializer;
import ar.edu.itba.paw.webapp.media_types.parsers.utils.ParserUtils;
import ar.edu.itba.paw.webapp.models.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Component
public class JWTParser {
    private static JWTParser instance;

    private final String secret;
    @Value("classpath:token.key")
    private Resource secretResource;

    public static JWTParser getInstance() {
        if (instance == null) instance = new JWTParser();
        return instance;
    }

    private JWTParser() {
        try {
            this.secret = this.getSecretKey();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public User decodeJWT(HttpServletRequest request) {
        Claims claims = this.decodeJWTClaims(request);
        if (claims == null) return null;

        Object o = claims.get(Constants.JWT_CLAIMS_DATA);
        try {
            return UserJWTDeserializer.instance.fromJson(ParserUtils.stringToJson((String) o));
        } catch (Exception e) {
            return null;
        }
    }

    public Collection<? extends GrantedAuthority> getAuthorities(HttpServletRequest request) {
        Claims claims = this.decodeJWTClaims(request);
        if (claims == null) return null;

        Object o = claims.get(Constants.JWT_CLAIMS_ROLE);
        String role = o != null ? o.toString() : null;
        if (role != null)
            return Collections.singletonList(new SimpleGrantedAuthority(role));
        else
            return Collections.emptyList();
    }

    private Claims decodeJWTClaims(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }

        Cookie jwtCookie = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(Constants.JWT_COOKIE_NAME)).findFirst().orElse(null);
        if (jwtCookie == null)
            return null;

        // Procesamos los claims guardados
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(this.secret)
                    .parseClaimsJws(jwtCookie.getValue())
                    .getBody();
        } catch (Exception e) {
            return null;
        }

        if (claims.getExpiration().before(new Date()))
            return null;
        return claims;
    }

    private String getSecretKey() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("token.key");
        Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
        return FileCopyUtils.copyToString(reader);
    }
}
