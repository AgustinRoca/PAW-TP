package ar.edu.itba.paw.webapp.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.NewCookie;

public abstract class CookieUtils {
    private static final String SET_COOKIE = "Set-Cookie";

    /**
     * There is no way to set an http only cookie with javax servlet api v2.5
     */
    public static void setHttpOnlyCookie(HttpServletResponse response, Cookie cookie) {
        NewCookie newCookie = new NewCookie(
                cookie.getName(),
                cookie.getValue(),
                cookie.getPath(),
                cookie.getDomain(),
                cookie.getComment(),
                cookie.getMaxAge(),
                cookie.getSecure(),
                true
        );

        response.addHeader(SET_COOKIE, newCookie.toString());
    }

    public static void setCookie(HttpServletResponse response, Cookie cookie) {
        response.addCookie(cookie);
    }
}
