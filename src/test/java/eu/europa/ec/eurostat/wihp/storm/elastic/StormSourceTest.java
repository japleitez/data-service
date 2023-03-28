package eu.europa.ec.eurostat.wihp.storm.elastic;

import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.europa.ec.eurostat.wihp.storm.elastic.StormSource;
import eu.europa.ec.eurostat.wihp.util.JsonNodeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StormSourceTest {

    @Test
    public void whenFullUrl_thenCreateSource() {
        String url = "https://www.arhs-group.com/";
        String expectedStatus = "DISCOVERED";
        String expectedKey = "arhs-group.com";

        StormSource source = new StormSource(url);

        Assertions.assertEquals(url, source.getUrl());
        Assertions.assertEquals(expectedStatus, source.getStatus());
        Assertions.assertEquals(expectedKey, source.getKey());
    }

    @Test
    public void whenTransformingToJson_thenMetadataIsEmptyObject() {
        String url = "https://www.arhs-group.com/";

        StormSource source = new StormSource(url);

        String jsonString = JsonNodeUtils.createJsonString(source);

        assertTrue(jsonString.contains("\"metadata\":{}"));
    }
}
