package ar.edu.itba.paw.webapp.media_types;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class MIMEHelper {
    public static void assertServerType(HttpServletRequest request, String... types) throws NotAcceptableException {
        assertServerType(getHeadersValues(request, HttpHeaders.ACCEPT), types);
    }

    public static void assertClientType(HttpServletRequest request, String... types) throws NotAcceptableException {
        assertClientType(getHeadersValues(request, HttpHeaders.CONTENT_TYPE), types);
    }

    public static void assertServerType(HttpHeaders httpHeaders, String... types) throws NotAcceptableException {
        Set<String> mediaTypes = httpHeaders.getAcceptableMediaTypes()
                .stream()
                .map(MediaType::toString)
                .collect(Collectors.toSet());

        assertServerType(mediaTypes, types);
    }

    private static void assertClientType(Collection<String> mediaTypes, String... types) throws NotAcceptableException {
        if (mediaTypes.contains(MediaType.WILDCARD) || mediaTypes.contains(ApplicationMIME.WILDCARD))
            return;

        for (String type : types) {
            if (!mediaTypes.contains(type))
                throw new NotAcceptableException();
            mediaTypes.remove(type);
        }
    }

    private static void assertServerType(Collection<String> mediaTypes, String... types) throws NotAcceptableException {
        if (mediaTypes.contains(MediaType.WILDCARD) || mediaTypes.contains(ApplicationMIME.WILDCARD))
            return;

        // Como cada endpoint puede devolver un error, entonces lo comparamos contra ese tipo
        if (!mediaTypes.contains(ErrorMIME.ERROR))
            throw new NotAcceptableException();

        for (String type : types) {
            if (!mediaTypes.contains(type))
                throw new NotAcceptableException();
            mediaTypes.remove(type);
        }
    }

    private static Set<String> getHeadersValues(HttpServletRequest request, String header) {
        Enumeration<?> headerNames = request.getHeaderNames();

        if (request.getHeaderNames() == null)
            throw new NotAcceptableException();

        Set<String> values = new HashSet<>();
        while (headerNames.hasMoreElements()) {
            Object o = headerNames.nextElement();
            if (o instanceof String && ((String) o).equalsIgnoreCase(header)) {
                values.addAll(org.springframework.http.MediaType.parseMediaTypes(
                        request
                                .getHeader((String) o))
                                .stream()
                                .map(Object::toString)
                                .collect(Collectors.toList())
                );
            }
        }

        return values;
    }
}
