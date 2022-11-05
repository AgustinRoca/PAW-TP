package ar.edu.itba.paw.webapp.media_types.parsers.serializers;

import ar.edu.itba.paw.models.DoctorSpecialty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DoctorSpecialtySerializer extends JsonSerializer<DoctorSpecialty> {
    public static final DoctorSpecialtySerializer instance = new DoctorSpecialtySerializer();

    private DoctorSpecialtySerializer() {}

    @Override
    public JsonNode toJson(DoctorSpecialty doctorSpecialty) {
        ObjectNode jsonObject = JsonNodeFactory.instance.objectNode();

        jsonObject.put("id", doctorSpecialty.getId());
        jsonObject.put("name", doctorSpecialty.getName());

        return jsonObject;
    }
}
