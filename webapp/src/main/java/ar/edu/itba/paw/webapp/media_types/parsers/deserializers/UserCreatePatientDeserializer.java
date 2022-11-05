package ar.edu.itba.paw.webapp.media_types.parsers.deserializers;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.models.PatientSignUp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.ws.rs.BadRequestException;

public class UserCreatePatientDeserializer extends UserCreateDeserializer<PatientSignUp> {
    public static final UserCreatePatientDeserializer instance = new UserCreatePatientDeserializer();

    private UserCreatePatientDeserializer() {}

    @Override
    public PatientSignUp fromJson(JsonNode o) {
        if (!(o instanceof ObjectNode)) {
            throw new BadRequestException();
        }

        User user = this.getUser((ObjectNode) o);

        PatientSignUp patientSignUp = new PatientSignUp();
        patientSignUp.setUser(user);

        return patientSignUp;
    }
}
