package ar.edu.itba.paw.webapp.rest.utils;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Paginator;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.JWTParser;
import ar.edu.itba.paw.webapp.auth.UserRole;
import ar.edu.itba.paw.webapp.exceptions.APIErrorFactory;
import ar.edu.itba.paw.webapp.exceptions.APIErrorFactory.APIErrorBuilder;
import ar.edu.itba.paw.webapp.exceptions.UnprocessableEntityException;
import ar.edu.itba.paw.webapp.models.Constants;
import ar.edu.itba.paw.webapp.models.error.APISubError;
import ar.edu.itba.paw.webapp.models.error.ErrorConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class GenericResource {
    protected static final int DEFAULT_PER_PAGE = 10;
    protected static final int MIN_PER_PAGE = 10;
    protected static final int MAX_PER_PAGE = 100;
    protected static final String PAGINATOR_PAGE_QUERY = "page";
    protected static final String PAGINATOR_PER_PAGE_QUERY = "per_page";

    @Autowired
    private UserService userService;

    protected boolean isDoctor() {
        if (!this.isAuthenticated())
            return false;

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            try {
                org.springframework.security.core.userdetails.User user = ((org.springframework.security.core.userdetails.User) principal);
                if (user.getAuthorities().isEmpty() || user.getAuthorities().stream().findFirst().get().getAuthority().equals(UserRole.UNVERIFIED.getAsRole())) {
                    // If the user is unverified we must return it from the db as it may now be verified
                    Optional<User> userOptional = this.userService.findById(Integer.parseInt(((org.springframework.security.core.userdetails.User) principal).getUsername()));
                    return userOptional.filter(value -> this.userService.isDoctor(value)).isPresent();
                } else {
                    Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

                    return authorities
                            .stream()
                            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(UserRole.DOCTOR.getAsRole()));
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    protected boolean isAuthenticated() {
        if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) return false;

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal instanceof org.springframework.security.core.userdetails.User;
    }

    protected Optional<User> getUser(HttpServletRequest request) {
        if (!this.isAuthenticated()) return Optional.empty();

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            try {
                org.springframework.security.core.userdetails.User user = ((org.springframework.security.core.userdetails.User) principal);
                if (user.getAuthorities().isEmpty() || user.getAuthorities().stream().findFirst().get().getAuthority().equals(UserRole.UNVERIFIED.getAsRole())) {
                    // If the user is unverified we must return it from the db as it may now be verified
                    return this.userService.findById(Integer.parseInt(((org.springframework.security.core.userdetails.User) principal).getUsername()));
                } else {
                    // We can just return the user stored in the jwt payload
                    return Optional.ofNullable(JWTParser.getInstance().decodeJWT(request));
                }
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    protected User assertUserUnauthorized(HttpServletRequest request) {
        Optional<User> user = this.getUser(request);
        if (!user.isPresent())
            throw this.unauthorized();
        return user.get();
    }

    protected User assertUserNotFound(HttpServletRequest request) {
        Optional<User> user = this.getUser(request);
        if (!user.isPresent())
            throw this.notFound();
        return user.get();
    }

    protected APIErrorBuilder error(Status status) {
        return this.error(status.getStatusCode(), status.toString());
    }

    protected WebApplicationException missingBodyParams() {
        return this.error(Status.BAD_REQUEST).withReason(ErrorConstants.MISSING_BODY_PARAMS).getError();
    }

    protected WebApplicationException unprocessableEntity(APISubError apiSubError) {
        return UnprocessableEntityException
                .build()
                .withReason(apiSubError)
                .getError();
    }

    protected WebApplicationException missingPathParams() {
        return this.error(Status.BAD_REQUEST).withReason(ErrorConstants.MISSING_PATH_PARAMS).getError();
    }

    protected WebApplicationException missingQueryParams() {
        return this.unprocessableEntity(ErrorConstants.MISSING_QUERY_PARAMS);
    }

    protected WebApplicationException invalidQueryParams() {
        return this.unprocessableEntity(ErrorConstants.INVALID_QUERY_PARAMS);
    }

    protected WebApplicationException notFound() {
        return this.error(Status.NOT_FOUND)
                .withReason(ErrorConstants.NOT_FOUND)
                .getError();
    }

    protected WebApplicationException unauthorized() {
        return this.error(Status.UNAUTHORIZED)
                .withReason(ErrorConstants.UNAUTHORIZED)
                .getError();
    }

    protected WebApplicationException forbidden() {
        return this.error(Status.FORBIDDEN)
                .withReason(ErrorConstants.FORBIDDEN)
                .getError();
    }

    protected APIErrorBuilder error(int code, String message) {
        return APIErrorFactory.buildError(code, message);
    }

    protected Set<Integer> stringToIntegerList(String list) {
        return this.stringToIntegerList(list, ",");
    }

    protected Set<Integer> stringToIntegerList(String list, String regexSeparator) {
        Set<Integer> specialtiesIds = new HashSet<>();
        if (list != null) {
            // split strings to get all items and create the list
            for (String s : list.split(regexSeparator)) {
                try {
                    int id = Integer.parseInt(s);
                    if (id >= 0) {
                        specialtiesIds.add(id);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return specialtiesIds;
    }

    protected ResponseBuilder createPaginatorResponse(Paginator<?> paginator, UriInfo uriInfo) {
        ResponseBuilder responseBuilder = Response
                .ok()
                .entity(paginator.getModels())
                .header(Constants.PAGINATOR_COUNT_HEADER, String.valueOf(paginator.getTotalCount()));

        if (paginator.getTotalCount() > 0) {
            int nextPage = 0, previousPage = 0;
            int firstPage = 1, lastPage = paginator.getTotalPages();

            if (paginator.getRemainingPages() > 0) {
                nextPage = paginator.getPage() + 1;
            }
            if (paginator.getPage() > firstPage) {
                previousPage = paginator.getPage() - 1;
            }

            StringBuilder header = new StringBuilder();
            header
                    .append("<")
                    .append(this.formatPaginatorUrl(firstPage, paginator.getPageSize(), uriInfo))
                    .append(">; rel=")
                    .append("\"first\"")
                    .append(", <")
                    .append(this.formatPaginatorUrl(lastPage, paginator.getPageSize(), uriInfo))
                    .append(">; rel=")
                    .append("\"last\"");
            if (nextPage > 0) {
                header
                        .append(", <")
                        .append(this.formatPaginatorUrl(nextPage, paginator.getPageSize(), uriInfo))
                        .append(">; rel=")
                        .append("\"next\"");
            }
            if (previousPage > 0) {
                header
                        .append(", <")
                        .append(this.formatPaginatorUrl(previousPage, paginator.getPageSize(), uriInfo))
                        .append(">; rel=")
                        .append("\"previous\"");
            }

            responseBuilder.header(HttpHeaders.LINK, header.toString());
        }

        return responseBuilder;
    }

    protected URI joinPaths(String start, String... paths) {
        StringBuilder end = new StringBuilder(start);

        for (String path : paths) {
            String endString = end.toString();

            if (endString.endsWith("/") && path.startsWith("/")) {
                end.append(path, 1, path.length());
            } else if (endString.endsWith("/") || path.startsWith("/")) {
                end.append(path);
            } else {
                end.append("/").append(path);
            }
        }

        return URI.create(end.toString());
    }

    private String formatPaginatorUrl(int page, int perPage, UriInfo uriInfo) {
        try {
            return uriInfo.getAbsolutePath().toString()
                    + "?"
                    + URLEncoder.encode(PAGINATOR_PAGE_QUERY, StandardCharsets.UTF_8.name())
                    + "="
                    + page
                    + "&"
                    + URLEncoder.encode(PAGINATOR_PER_PAGE_QUERY, StandardCharsets.UTF_8.name())
                    + "="
                    + perPage;
        } catch (UnsupportedEncodingException e) {
            throw new InternalServerErrorException();
        }
    }
}
