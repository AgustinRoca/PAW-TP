package ar.edu.itba.paw.webapp.media_types.parsers.serializers;

import ar.edu.itba.paw.models.Province;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ProvinceSerializer extends JsonSerializer<Province> {
    public static final ProvinceSerializer instance = new ProvinceSerializer();

    private ProvinceSerializer() {}

    @Override
    public JsonNode toJson(Province province) {
        ObjectNode jsonObject = JsonNodeFactory.instance.objectNode();

        jsonObject.put("id", province.getId());
        jsonObject.put("name", province.getName());
        jsonObject.put("countryId", province.getCountry().getId());

        return jsonObject;
    }
}
