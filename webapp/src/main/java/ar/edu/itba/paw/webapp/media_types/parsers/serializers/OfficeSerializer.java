package ar.edu.itba.paw.webapp.media_types.parsers.serializers;

import ar.edu.itba.paw.models.Office;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class OfficeSerializer extends JsonSerializer<Office> {
    public static final OfficeSerializer instance = new OfficeSerializer();

    private OfficeSerializer() {}

    @Override
    public JsonNode toJson(Office office) {
        ObjectNode jsonObject = JsonNodeFactory.instance.objectNode();

        jsonObject.put("id", office.getId());
        jsonObject.put("name", office.getName());
        jsonObject.put("phone", office.getPhone());
        jsonObject.put("email", office.getEmail());
        jsonObject.put("street", office.getStreet());
        jsonObject.put("url", office.getUrl());
        jsonObject.put("localityId", office.getLocality().getId());

        return jsonObject;
    }
}
