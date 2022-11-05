package ar.edu.itba.paw.webapp.media_types.parsers.input;

import ar.edu.itba.paw.models.Workday;
import ar.edu.itba.paw.webapp.media_types.WorkdayMIME;
import ar.edu.itba.paw.webapp.media_types.parsers.deserializers.WorkdayCreateDeserializer;
import ar.edu.itba.paw.webapp.media_types.parsers.utils.GenericParser;
import ar.edu.itba.paw.webapp.media_types.parsers.utils.ParserUtils;
import com.fasterxml.jackson.databind.node.ArrayNode;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;

@Provider
@Consumes(WorkdayMIME.CREATE_LIST)
public class WorkdayCreateListParser extends GenericParser<Collection<Workday>> {
    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public Collection<Workday> readFrom(Class<Collection<Workday>> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> multivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        return WorkdayCreateDeserializer.instance.fromJsonArray((ArrayNode) ParserUtils.inputToJSON(inputStream));
    }
}
