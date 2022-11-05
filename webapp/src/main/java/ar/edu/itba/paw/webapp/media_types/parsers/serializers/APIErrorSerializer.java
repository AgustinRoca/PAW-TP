package ar.edu.itba.paw.webapp.media_types.parsers.serializers;

import ar.edu.itba.paw.webapp.models.error.APIBaseError;
import ar.edu.itba.paw.webapp.models.error.APIError;
import ar.edu.itba.paw.webapp.models.error.APISubError;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class APIErrorSerializer extends JsonSerializer<APIError> {
    public static final APIErrorSerializer instance = new APIErrorSerializer();

    private APIErrorSerializer() {}

    @Override
    public JsonNode toJson(APIError apiError) {
        ObjectNode jsonObject = this.baseErrorToJson(apiError);

        ArrayNode subErrors = jsonObject.putArray("errors");
        for (APISubError subError : apiError.getSubErrors()) {
            subErrors.add(this.baseErrorToJson(subError));
        }

        return jsonObject;
    }

    private ObjectNode baseErrorToJson(APIBaseError baseError) {
        ObjectNode jsonObject = JsonNodeFactory.instance.objectNode();

        jsonObject.put("code", baseError.getCode());
        jsonObject.put("message", baseError.getMessage());

        return jsonObject;
    }
}
