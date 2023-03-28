package eu.europa.ec.eurostat.wihp.storm.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
public class JsonNodeService {


    public Optional<JsonNode> setProperty(final JsonNode configuration, final String property, final Object value) {
        validate(configuration, property, value);

        boolean isLast;
        String propertyKey;
        JsonNode node = configuration.findPath(property);

        final String[] nodesArray = property.split("\\.");

        if (node instanceof MissingNode) {
            propertyKey = nodesArray[0];
            isLast = (nodesArray.length == 1);
        } else {
            isLast = true;
            propertyKey = property;
        }

        if (isLast) {
            return setPropertyAndValue((ObjectNode) configuration, value, propertyKey);
        } else {
            final String subNode = property.substring(propertyKey.length() + 1);
            JsonNode currentJsonNode = configuration.findPath(propertyKey);
            currentJsonNode = putNodeIfMissing((ObjectNode) configuration, propertyKey, currentJsonNode);
            return setProperty(currentJsonNode, subNode, value).isEmpty() ? Optional.empty() : Optional.of(configuration);
        }
    }

    private JsonNode putNodeIfMissing(final ObjectNode configuration, final String singleProperty, final JsonNode currentJsonNode) {
        if (currentJsonNode instanceof MissingNode) {
            return configuration.putObject(singleProperty);
        }
        return currentJsonNode;
    }

    private Optional<JsonNode> setPropertyAndValue(final ObjectNode configuration, final Object value, final String fieldName) {
        if (value instanceof Collection) {
            final ArrayNode arrayNode = configuration.putArray(fieldName);
            ((Collection<?>) value).forEach(arrayNode::addPOJO);
        } else {
            configuration.putPOJO(fieldName, value);
        }
        return Optional.of(configuration);
    }

    private void validate(final JsonNode configuration, final String property, final Object value) {
        Objects.requireNonNull(configuration);
        Objects.requireNonNull(property);
        Objects.requireNonNull(value);

        if (property.isEmpty()) {
            throw new IllegalArgumentException("Property " + property + "is empty");
        }

        if (property.endsWith(".")) {
            throw new IllegalArgumentException("Property " + property + " ends with .");
        }
    }

}
