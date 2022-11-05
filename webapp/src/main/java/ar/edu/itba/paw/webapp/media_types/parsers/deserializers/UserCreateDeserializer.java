package ar.edu.itba.paw.webapp.media_types.parsers.deserializers;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.models.error.ErrorConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class UserCreateDeserializer<T> extends JsonDeserializer<T> {
    protected User getUser(ObjectNode jsonObject) {
        User user = new User();

        user.setEmail(this.getStringNonNull(
                jsonObject,
                "email",
                this.emailValidator,
                ErrorConstants.USER_CREATE_MISSING_EMAIL,
                ErrorConstants.USER_CREATE_INVALID_EMAIL
        ));
        user.setFirstName(this.getStringNonNull(
                jsonObject,
                "firstName",
                s -> s.length() >= 2 && s.length() <= 20,
                ErrorConstants.USER_CREATE_MISSING_FIRST_NAME,
                ErrorConstants.USER_CREATE_INVALID_FIRST_NAME
        ));
        user.setSurname(this.getStringNonNull(
                jsonObject,
                "surname",
                s -> s.length() >= 2 && s.length() <= 20,
                ErrorConstants.USER_CREATE_MISSING_SURNAME,
                ErrorConstants.USER_CREATE_INVALID_SURNAME
        ));
        user.setPassword(this.getStringNonNull(
                jsonObject,
                "password",
                s -> s.length() >= 8 && s.length() <= 100,
                ErrorConstants.USER_CREATE_MISSING_PASSWORD,
                ErrorConstants.USER_CREATE_INVALID_PASSWORD
        ));

        JsonNode node = jsonObject.get("phone");
        if (node != null) {
            user.setPhone(this.getStringNonNull(
                    jsonObject,
                    "phone",
                    null,
                    ErrorConstants.USER_CREATE_INVALID_PHONE
            ));
        } else {
            user.setPhone(null);
        }

        return user;
    }
}
