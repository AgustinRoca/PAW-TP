package ar.edu.itba.paw.webapp.media_types.parsers.deserializers;

import ar.edu.itba.paw.webapp.exceptions.UnprocessableEntityException;
import ar.edu.itba.paw.webapp.models.error.APISubError;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Predicate;

public abstract class JsonDeserializer<T> {
    protected final Predicate<String> emailValidator = s -> {
        return s.toLowerCase().matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
    };

    public abstract T fromJson(JsonNode object);

    public Collection<T> fromJsonArray(ArrayNode jsonArray) {
        Collection<T> collection = new LinkedList<>();
        for (Object o : jsonArray) {
            collection.add(this.fromJson((JsonNode) o));
        }
        return collection;
    }

    protected ObjectNode getObjectNonNull(ObjectNode objectNode, String key, APISubError missingError, APISubError invalidError) {
        JsonNode node = objectNode.get(key);
        if (node == null) {
            throw UnprocessableEntityException
                    .build()
                    .withReason(missingError)
                    .getError();
        } else if (!node.isObject()) {
            throw UnprocessableEntityException
                    .build()
                    .withReason(invalidError)
                    .getError();
        }

        return (ObjectNode) node;
    }

    protected Collection<Integer> getArrayAsInt(ObjectNode jsonObject, String key, APISubError invalidError) {
        Collection<Integer> ids = new LinkedList<>();

        JsonNode node = jsonObject.get(key);
        if (node != null) {
            if (!node.isArray()) {
                throw UnprocessableEntityException
                        .build()
                        .withReason(invalidError)
                        .getError();
            }

            for (JsonNode arrayNode : node) {
                if (!arrayNode.isInt()) {
                    throw UnprocessableEntityException
                            .build()
                            .withReason(invalidError)
                            .getError();
                }

                ids.add(arrayNode.asInt());
            }
        }

        return ids;
    }

    protected int getIntegerNonNull(ObjectNode objectNode, String key, APISubError missingError, APISubError invalidError) {
        JsonNode node = objectNode.get(key);
        if (node == null) {
            throw UnprocessableEntityException
                    .build()
                    .withReason(missingError)
                    .getError();
        } else if (!node.isInt()) {
            throw UnprocessableEntityException
                    .build()
                    .withReason(invalidError)
                    .getError();
        }

        return node.asInt();
    }

    protected int getIntegerNonNull(ObjectNode objectNode, String key, Predicate<Integer> predicate, APISubError missingError, APISubError invalidError) {
        int n = this.getIntegerNonNull(objectNode, key, missingError, invalidError);
        if (!predicate.test(n)) {
            throw UnprocessableEntityException
                    .build()
                    .withReason(invalidError)
                    .getError();
        }
        return n;
    }

    protected String getStringNull(ObjectNode objectNode, String key, APISubError invalidError) {
        return this.getString(objectNode, key, s -> true, invalidError);
    }

    protected String getStringNonNull(ObjectNode objectNode, String key, APISubError missingError, APISubError invalidError) {
        return this.getStringNonNull(objectNode, key, s -> true, missingError, invalidError);
    }

    protected String getStringNonNull(ObjectNode objectNode, String key, Predicate<String> predicate, APISubError missingError, APISubError invalidError) {
        String s = this.getString(objectNode, key, predicate, invalidError);
        if (s == null) {
            throw UnprocessableEntityException
                    .build()
                    .withReason(missingError)
                    .getError();
        }
        return s;
    }

    private String getString(ObjectNode objectNode, String key, Predicate<String> predicate, APISubError invalidError) {
        JsonNode node = objectNode.get(key);
        if (node == null || node.isNull()) return null;
        if (!node.isTextual() || !predicate.test(node.asText())) {
            throw UnprocessableEntityException
                    .build()
                    .withReason(invalidError)
                    .getError();
        }
        return node.asText();
    }
}
