package ar.edu.itba.paw.webapp.media_types.parsers.serializers;

import ar.edu.itba.paw.models.Locality;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class LocalitySerializer extends JsonSerializer<Locality> {
    public static final LocalitySerializer instance = new LocalitySerializer();

    private LocalitySerializer() {}

    @Override
    public JsonNode toJson(Locality locality) {
        ObjectNode jsonObject = JsonNodeFactory.instance.objectNode();

        jsonObject.put("id", locality.getId());
        jsonObject.put("name", locality.getName());
        jsonObject.put("province", locality.getProvince().getId());

        return jsonObject;
    }
}
