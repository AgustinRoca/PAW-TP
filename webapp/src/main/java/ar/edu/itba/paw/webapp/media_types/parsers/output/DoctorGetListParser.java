package ar.edu.itba.paw.webapp.media_types.parsers.output;

import ar.edu.itba.paw.models.Doctor;
import ar.edu.itba.paw.webapp.media_types.DoctorMIME;
import ar.edu.itba.paw.webapp.media_types.parsers.serializers.DoctorSerializer;
import ar.edu.itba.paw.webapp.media_types.parsers.utils.GenericParser;
import org.glassfish.jersey.message.internal.ReaderWriter;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;

@Provider
@Produces(DoctorMIME.GET_LIST)
public class DoctorGetListParser extends GenericParser<Collection<Doctor>> {
    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public void writeTo(Collection<Doctor> doctors, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(outputStream, ReaderWriter.getCharset(mediaType));
            writer.write(DoctorSerializer.instance.toJsonArray(doctors).toString());
            writer.flush();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }
}
