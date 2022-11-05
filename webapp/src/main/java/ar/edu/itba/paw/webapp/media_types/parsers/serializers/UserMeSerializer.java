package ar.edu.itba.paw.webapp.media_types.parsers.serializers;

import ar.edu.itba.paw.webapp.models.UserMe;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UserMeSerializer extends JsonSerializer<UserMe> {
    public static final UserMeSerializer instance = new UserMeSerializer();

    private UserMeSerializer() {}

    @Override
    public JsonNode toJson(UserMe userMe) {
        ObjectNode jsonObject = JsonNodeFactory.instance.objectNode();
        jsonObject.set("user", UserSerializer.instance.toJson(userMe.getUser()));

        if (userMe.getDoctors() != null) {
            jsonObject.set("doctors", DoctorSerializer.instance.toJsonArray(userMe.getDoctors()));
        } else if (userMe.getPatients() != null) {
            jsonObject.set("patients", PatientSerializer.instance.toJsonArray(userMe.getPatients()));
        }

        return jsonObject;
    }
}
