package ar.edu.itba.paw.webapp.media_types.parsers.serializers;

import ar.edu.itba.paw.models.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UserSerializer extends JsonSerializer<User> {
    public static final UserSerializer instance = new UserSerializer();

    private UserSerializer() {}

    @Override
    public JsonNode toJson(User user) {
        ObjectNode jsonObject = JsonNodeFactory.instance.objectNode();

        jsonObject.put("id", user.getId());
        jsonObject.put("email", user.getEmail());
        jsonObject.put("firstName", user.getFirstName());
        jsonObject.put("surname", user.getSurname());
        jsonObject.put("verified", user.getVerified());
        jsonObject.put("phone", user.getPhone());
        jsonObject.put("hasPicture", user.getProfilePictureId() != null);

        return jsonObject;
    }
}
