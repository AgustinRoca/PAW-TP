package ar.edu.itba.paw.webapp.media_types.parsers.serializers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.util.Collection;

public abstract class JsonSerializer<T> {
    public abstract JsonNode toJson(T t);

    public ArrayNode toJsonArray(Collection<T> ts) {
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        for (T t : ts) {
            array.add(this.toJson(t));
        }
        return array;
    }
}
