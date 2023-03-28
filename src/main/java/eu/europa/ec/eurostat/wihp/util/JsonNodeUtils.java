package eu.europa.ec.eurostat.wihp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonNodeUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonNodeUtils.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonNodeUtils() {
    }

    public static Optional<JsonNode> createJsonNode(String jsonString) {
        try {
            JsonNode node = OBJECT_MAPPER.readTree(jsonString);
            return Optional.of(node);
        } catch (JsonProcessingException e) {
            log.error("Error to convert string to json, string={}", jsonString);
            return Optional.empty();
        }
    }

    public static String createJsonString(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error to convert object to json string, object={}", object);
            throw new IllegalArgumentException("Error to convert object to json string", e);
        }
    }

    public static <T> T getObject(String jsonStr, Class<T> clazz) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(jsonStr, clazz);
    }

    public static <T> T getObject(JsonNode jsonNode, Class<T> clazz) throws JsonProcessingException {
        return OBJECT_MAPPER.treeToValue(jsonNode, clazz);
    }

    public static JsonNode createJsonNode(Object object) {
        return OBJECT_MAPPER.valueToTree(object);
    }
}
