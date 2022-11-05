package ar.edu.itba.paw.webapp.media_types.parsers.serializers;

import ar.edu.itba.paw.models.Patient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PatientSerializer extends JsonSerializer<Patient> {
    public static final PatientSerializer instance = new PatientSerializer();

    private PatientSerializer() {}

    @Override
    public JsonNode toJson(Patient patient) {
        ObjectNode jsonObject = JsonNodeFactory.instance.objectNode();

        jsonObject.put("id", patient.getId());
        jsonObject.put("userId", patient.getUser().getId());
        jsonObject.put("firstName", patient.getUser().getFirstName());
        jsonObject.put("surname", patient.getUser().getSurname());
        jsonObject.put("officeId", patient.getOffice().getId());

        return jsonObject;
    }
}
