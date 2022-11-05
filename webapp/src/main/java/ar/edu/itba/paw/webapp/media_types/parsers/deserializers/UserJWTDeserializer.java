package ar.edu.itba.paw.webapp.media_types.parsers.deserializers;

import ar.edu.itba.paw.models.Picture;
import ar.edu.itba.paw.models.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.ws.rs.BadRequestException;

public class UserJWTDeserializer extends JsonDeserializer<User> {
    public static final UserJWTDeserializer instance = new UserJWTDeserializer();

    private UserJWTDeserializer() {}

    @Override
    public User fromJson(JsonNode o) {
        if (!(o instanceof ObjectNode)) {
            throw new BadRequestException();
        }

        ObjectNode jsonObject = (ObjectNode) o;

        User user = new User();
        user.setEmail(jsonObject.get("email").asText());
        user.setFirstName(jsonObject.get("firstName").asText());
        user.setId(jsonObject.get("id").asInt());
        if (!jsonObject.get("phone").isNull())
            user.setPhone(jsonObject.get("phone").asText());
        user.setSurname(jsonObject.get("surname").asText());
        user.setVerified(jsonObject.get("verified").asBoolean());

        if (jsonObject.hasNonNull("profilePictureId")) {
            Picture picture = new Picture();
            picture.setId(jsonObject.get("profilePictureId").asInt());
            user.setProfilePicture(picture);
        }

        return user;
    }
}
