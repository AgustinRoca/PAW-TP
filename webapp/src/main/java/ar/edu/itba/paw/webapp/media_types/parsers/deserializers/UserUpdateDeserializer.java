package ar.edu.itba.paw.webapp.media_types.parsers.deserializers;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.models.error.ErrorConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.ws.rs.BadRequestException;

public class UserUpdateDeserializer extends JsonDeserializer<User> {
    public static final UserUpdateDeserializer instance = new UserUpdateDeserializer();

    private UserUpdateDeserializer() {}

    @Override
    public User fromJson(JsonNode o) {
        if (!(o instanceof ObjectNode)) {
            throw new BadRequestException();
        }

        ObjectNode jsonObject = (ObjectNode) o;

        User user = new User();

        JsonNode node = jsonObject.get("email");
        if (node != null && !node.isNull()) {
            user.setEmail(this.getStringNonNull(
                    jsonObject,
                    "email",
                    null,
                    ErrorConstants.USER_UPDATE_INVALID_EMAIL
            ));
        }

        node = jsonObject.get("firstName");
        if (node != null && !node.isNull()) {
            user.setFirstName(this.getStringNonNull(
                    jsonObject,
                    "firstName",
                    null,
                    ErrorConstants.USER_UPDATE_INVALID_FIRST_NAME
            ));
        }

        node = jsonObject.get("surname");
        if (node != null && !node.isNull()) {
            user.setSurname(this.getStringNonNull(
                    jsonObject,
                    "surname",
                    null,
                    ErrorConstants.USER_UPDATE_INVALID_SURNAME
            ));
        }

        node = jsonObject.get("phone");
        if (node != null && !node.isNull()) {
            user.setPhone(this.getStringNonNull(
                    jsonObject,
                    "phone",
                    null,
                    ErrorConstants.USER_UPDATE_INVALID_PHONE
            ));
        }

        node = jsonObject.get("password");
        if (node != null && !node.isNull()) {
            user.setPassword(this.getStringNonNull(
                    jsonObject,
                    "password",
                    null,
                    ErrorConstants.USER_UPDATE_INVALID_PASSWORD
            ));
        }

        user.setVerified(null);
        user.setProfilePicture(null);

        return user;
    }
}
