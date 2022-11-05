package ar.edu.itba.paw.webapp.media_types.parsers.serializers;

import ar.edu.itba.paw.models.Doctor;
import ar.edu.itba.paw.models.DoctorSpecialty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DoctorSerializer extends JsonSerializer<Doctor> {
    public static final DoctorSerializer instance = new DoctorSerializer();

    private DoctorSerializer() {}

    @Override
    public JsonNode toJson(Doctor doctor) {
        ObjectNode jsonObject = JsonNodeFactory.instance.objectNode();

        jsonObject.put("id", doctor.getId());
        jsonObject.put("phone", doctor.getPhone());
        jsonObject.put("email", doctor.getEmail());
        jsonObject.put("registrationNumber", doctor.getRegistrationNumber());

        jsonObject.replace("user", UserSerializer.instance.toJson(doctor.getUser()));
        jsonObject.replace("office", OfficeSerializer.instance.toJson(doctor.getOffice()));

        ArrayNode specialtiesArray = jsonObject.putArray("specialtyIds");
        for (DoctorSpecialty doctorSpecialty : doctor.getDoctorSpecialties()) {
            specialtiesArray.add(doctorSpecialty.getId());
        }

        return jsonObject;
    }
}
