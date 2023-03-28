package eu.europa.ec.eurostat.wihp.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JsonNodeUtilsTest {

    @Test
    public void checkCreateJsonNodeFrom_InValidString() {
       Optional<JsonNode> opt =  JsonNodeUtils.createJsonNode("sdlkj - :");
       assertTrue(opt.isEmpty());
    }

    @Test
    public void checkCreateJsonNodeFrom_ValidString() {
        Optional<JsonNode> opt =  JsonNodeUtils.createJsonNode( "{\"status\":\"STOPPED\", \"stormId\":\"0001\", \"timestamp\":\"2021-08-17T09:21:04.112243700Z\"}");
        assertTrue(opt.isPresent());
    }

}
