package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.webapp.models.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Component
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    @Autowired
    public JWTAuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        UsernamePasswordAuthenticationToken authentication = this.getAuthentication(req);

        if (authentication != null)
            SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token = this.parseAuthentication(request);
        request.setAttribute(Constants.VALID_JWT_REQUEST_ATTRIBUTE, token != null);
        return token;
    }

    protected UsernamePasswordAuthenticationToken parseAuthentication(HttpServletRequest request) {
        ar.edu.itba.paw.models.User jwtUser = JWTParser.getInstance().decodeJWT(request);
        Collection<? extends GrantedAuthority> authorities = JWTParser.getInstance().getAuthorities(request);

        return jwtUser != null ? new UsernamePasswordAuthenticationToken(new User(jwtUser.getId().toString(), "", authorities), "", authorities) : null;
    }
}
