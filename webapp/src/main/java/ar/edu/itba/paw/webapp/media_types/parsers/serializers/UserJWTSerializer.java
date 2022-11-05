package ar.edu.itba.paw.webapp.media_types.parsers.serializers;

import ar.edu.itba.paw.models.User;
import com.fasterxml.jackson.databind.JsonNode;

public class UserJWTSerializer extends JsonSerializer<User> {
    public static final UserJWTSerializer instance = new UserJWTSerializer();

    private UserJWTSerializer() {}

    @Override
    public JsonNode toJson(User user) {
        return UserSerializer.instance.toJson(user);
    }
}
