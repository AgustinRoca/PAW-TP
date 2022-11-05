package ar.edu.itba.paw.webapp.models;

import java.util.concurrent.TimeUnit;

public abstract class Constants {
    public static final String JWT_CLAIMS_ROLE = "role";
    public static final String JWT_CLAIMS_DATA = "data";
    public static final long JWT_EXPIRATION_MILLIS = TimeUnit.HOURS.toMillis(1);
    public static final long JWT_REFRESH_EXPIRATION_MILLIS = TimeUnit.DAYS.toMillis(7);
    public static final String JWT_COOKIE_NAME = "X-Jwt";
    public static final String LOGGED_IN_TTL_HEADER_NAME = "X-Logged-In-TTL";
    public static final String REFRESH_TOKEN_TTL_HEADER_NAME = "x-refresh-token-ttl";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "X-Refresh-Token";
    public static final String EMPTY_COOKIE = "";
    public static final String REFRESH_TOKEN_ENDPOINT = "refresh";
    public static final String LOGOUT_ENDPOINT = "logout";
    public static final String AUTH_ENDPOINT = "auth";
    public static final String PAGINATOR_COUNT_HEADER = "Total-Items";
    public static final String VALID_JWT_REQUEST_ATTRIBUTE = "valid_jwt_request_attribute";

    public static final int UNPROCESSABLE_ENTITY_CODE = 422;
    public static final String UNPROCESSABLE_ENTITY_DESCRIPTION = "Validation failed";

    public static final long MAX_PROFILE_PICTURE_SIZE = 5 * 1024 * 1024; // 5 MB
}
