package ar.edu.itba.paw.webapp.media_types.parsers.input;

import ar.edu.itba.paw.webapp.media_types.UserMIME;
import ar.edu.itba.paw.webapp.media_types.parsers.deserializers.UserCreateDoctorDeserializer;
import ar.edu.itba.paw.webapp.media_types.parsers.utils.GenericParser;
import ar.edu.itba.paw.webapp.media_types.parsers.utils.ParserUtils;
import ar.edu.itba.paw.webapp.models.DoctorSignUp;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Consumes(UserMIME.CREATE_DOCTOR)
public class UserCreateDoctorParser extends GenericParser<DoctorSignUp> {
    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public DoctorSignUp readFrom(Class<DoctorSignUp> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> multivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        return UserCreateDoctorDeserializer.instance.fromJson(ParserUtils.inputToJSON(inputStream));
    }
}
