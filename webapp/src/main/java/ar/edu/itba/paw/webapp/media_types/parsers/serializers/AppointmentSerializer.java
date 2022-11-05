package ar.edu.itba.paw.webapp.media_types.parsers.serializers;

import ar.edu.itba.paw.models.Appointment;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.DateTimeZone;

public class AppointmentSerializer extends JsonSerializer<Appointment> {
    public static final AppointmentSerializer instance = new AppointmentSerializer();

    private AppointmentSerializer() {}

    @Override
    public JsonNode toJson(Appointment appointment) {
        ObjectNode jsonObject = JsonNodeFactory.instance.objectNode();

        jsonObject.put("id", appointment.getId());
        jsonObject.put("status", appointment.getAppointmentStatus().toString());
        jsonObject.put("date_from", appointment.getFromDate().toDateTime(DateTimeZone.UTC).getMillis()); // Epoch millis
        jsonObject.put("message", appointment.getMessage());
        jsonObject.put("motive", appointment.getMotive());
        jsonObject.put("patient", PatientSerializer.instance.toJson(appointment.getPatient()));
        jsonObject.put("doctorId", appointment.getDoctor().getId());

        return jsonObject;
    }
}
