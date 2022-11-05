package ar.edu.itba.paw.webapp.media_types.parsers.deserializers;

import ar.edu.itba.paw.models.Doctor;
import ar.edu.itba.paw.models.DoctorSpecialty;
import ar.edu.itba.paw.webapp.models.error.ErrorConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.ws.rs.BadRequestException;
import java.util.stream.Collectors;

public class DoctorUpdateDeserializer extends JsonDeserializer<Doctor> {
    public static final DoctorUpdateDeserializer instance = new DoctorUpdateDeserializer();

    private DoctorUpdateDeserializer() {}

    @Override
    public Doctor fromJson(JsonNode o) {
        if (!(o instanceof ObjectNode)) {
            throw new BadRequestException();
        }

        ObjectNode jsonObject = (ObjectNode) o;

        Doctor doctor = new Doctor();

        JsonNode node = jsonObject.get("phone");
        if (node != null && !node.isNull()) {
            doctor.setPhone(this.getStringNonNull(
                    jsonObject,
                    "phone",
                    null,
                    ErrorConstants.DOCTOR_UPDATE_INVALID_PHONE
            ));
        }
        node = jsonObject.get("email");
        if (node != null && !node.isNull()) {
            doctor.setEmail(this.getStringNonNull(
                    jsonObject,
                    "email",
                    this.emailValidator,
                    null,
                    ErrorConstants.DOCTOR_UPDATE_INVALID_EMAIL
            ));
        }

        node = jsonObject.get("specialtyIds");
        if (node != null) {
            doctor.setDoctorSpecialties(
                    this.getArrayAsInt(
                            jsonObject,
                            "specialtyIds",
                            ErrorConstants.DOCTOR_UPDATE_INVALID_SPECIALTIES
                    )
                            .stream()
                            .map(integer -> {
                                DoctorSpecialty specialty = new DoctorSpecialty();
                                specialty.setId(integer);
                                return specialty;
                            })
                            .collect(Collectors.toList())
            );
        }

        return doctor;
    }
}
