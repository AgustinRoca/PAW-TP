package ar.edu.itba.paw.webapp.media_types.parsers.deserializers;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.models.DoctorSignUp;
import ar.edu.itba.paw.webapp.models.error.ErrorConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.ws.rs.BadRequestException;

public class UserCreateDoctorDeserializer extends UserCreateDeserializer<DoctorSignUp> {
    public static final UserCreateDoctorDeserializer instance = new UserCreateDoctorDeserializer();

    private UserCreateDoctorDeserializer() {}

    @Override
    public DoctorSignUp fromJson(JsonNode o) {
        if (!(o instanceof ObjectNode)) {
            throw new BadRequestException();
        }

        ObjectNode jsonObject = (ObjectNode) o;

        User user = this.getUser(jsonObject);

        DoctorSignUp doctorSignUp = new DoctorSignUp();
        doctorSignUp.setUser(user);

        if (jsonObject.has("registrationNumber")) {
            doctorSignUp.setRegistrationNumber(this.getIntegerNonNull(
                    jsonObject,
                    "registrationNumber",
                    null,
                    ErrorConstants.USER_CREATE_DOCTOR_INVALID_REGISTRATION_NUMBER
            ));
        }

        doctorSignUp.setLocalityId(this.getIntegerNonNull(
                jsonObject,
                "localityId",
                ErrorConstants.USER_CREATE_DOCTOR_MISSING_LOCALITY_ID,
                ErrorConstants.USER_CREATE_DOCTOR_INVALID_LOCALITY_ID
        ));
        doctorSignUp.setStreet(this.getStringNonNull(
                jsonObject,
                "street",
                ErrorConstants.USER_CREATE_DOCTOR_MISSING_STREET,
                ErrorConstants.USER_CREATE_DOCTOR_INVALID_STREET
        ));

        return doctorSignUp;
    }
}
