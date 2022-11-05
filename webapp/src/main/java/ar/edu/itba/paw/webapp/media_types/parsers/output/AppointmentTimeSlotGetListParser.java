package ar.edu.itba.paw.webapp.media_types.parsers.output;

import ar.edu.itba.paw.models.AppointmentTimeSlot;
import ar.edu.itba.paw.webapp.media_types.AppointmentTimeSlotMIME;
import ar.edu.itba.paw.webapp.media_types.parsers.serializers.AppointmentTimeSlotSerializer;
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
@Produces(AppointmentTimeSlotMIME.GET_LIST)
public class AppointmentTimeSlotGetListParser extends GenericParser<Collection<AppointmentTimeSlot>> {
    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public void writeTo(Collection<AppointmentTimeSlot> timeSlots, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(outputStream, ReaderWriter.getCharset(mediaType));
            writer.write(AppointmentTimeSlotSerializer.instance.toJsonArray(timeSlots).toString());
            writer.flush();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }
}
