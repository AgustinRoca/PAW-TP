package ar.edu.itba.paw.webapp.media_types.parsers.serializers;

import ar.edu.itba.paw.models.Country;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CountrySerializer extends JsonSerializer<Country> {
    public static final CountrySerializer instance = new CountrySerializer();

    private CountrySerializer() {}

    @Override
    public JsonNode toJson(Country country) {
        ObjectNode jsonObject = JsonNodeFactory.instance.objectNode();

        jsonObject.put("id", country.getId());
        jsonObject.put("name", country.getName());

        return jsonObject;
    }
}
