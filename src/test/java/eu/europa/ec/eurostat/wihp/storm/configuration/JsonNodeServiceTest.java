package eu.europa.ec.eurostat.wihp.storm.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JsonNodeServiceTest {

    JsonNodeService unit = new JsonNodeService();
    String config_multy =
        "eureka:\n" +
        "  client:\n" +
        "    enabled: false\n" +
        "  instance:\n" +
        "    appname: dataAcquisitionService\n" +
        "    instanceId: dataAcquisitionService:${spring.application.instance-id:${random.value}}";
    String config_simple = "eureka: 33\n";

    static JsonNode createConfigJsonNode(String config) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readTree(config);
    }

    @Test
    public void whenJsonNodeIsNullThen_setProperty_ThrowsException() {
        JsonNode node = null;
        String prop = null;
        Object val = null;

        Assertions.assertThrows(NullPointerException.class, () -> unit.setProperty(node, prop, val));
    }

    @Test
    public void whenPropertyIsPresentThen_setProperty_ReturnJsonNodeOptional() throws IOException {
        JsonNode node = createConfigJsonNode(config_simple);

        String prop = "eureka";
        Object val = "dummy";
        Assertions.assertFalse(unit.setProperty(node, prop, val).isEmpty());
    }

    @Test
    public void whenPropertyNotPresent_setProperty_ReturnOptionalWithNewJnode() throws IOException {
        JsonNode config = createConfigJsonNode(config_multy);
        String prop = "NewProerty";
        String val = "NewVal";

        Optional<JsonNode> newNode = unit.setProperty(config, prop, val);
        String actual = newNode.orElseThrow().findValue(prop).asText();
        Assertions.assertEquals(val, actual);
        System.out.println(actual);
        System.out.println(val);
    }

    @Test
    public void whenPropertyIsEmpty_thenShouldReturnOptionalEmpty() throws JsonProcessingException {
        JsonNode config = createConfigJsonNode(config_multy);
        String prop = "";
        String val = "NewVal";

        Assertions.assertThrows(RuntimeException.class, () -> unit.setProperty(config, prop, val));
    }

    @Test
    public void whenNestedPropertyIsEmpty_thenReturnOptionalEmpty() throws JsonProcessingException {
        JsonNode config = createConfigJsonNode(config_multy);
        String prop = "aaa.bbb.";
        String val = "NewVal";

        Assertions.assertThrows(RuntimeException.class, () -> unit.setProperty(config, prop, val));
    }

    @Test
    public void whenNestedPropertyIsPresent_thenReturnOptionalJsonNode() throws IOException {
        JsonNode config = createConfigJsonNode(config_multy);
        String prop = "eureka.client.enabled"; // boolean
        Boolean val = true;

        Optional<JsonNode> newNode = unit.setProperty(config, prop, val);
        Boolean actual = newNode.orElseThrow().get("eureka").get("client").get("enabled").asBoolean();
        Assertions.assertEquals(val, actual);
        System.out.println(newNode);
    }

    @Test
    public void whenNestedPropertyNotPresent_thenReturnOptionalJsonNode() throws IOException {
        JsonNode config = createConfigJsonNode(config_multy);
        String prop = "aaaa.bbbb.cccc"; // boolean
        Integer val = 33;

        Optional<JsonNode> newNode = unit.setProperty(config, prop, val);
        Integer actual = newNode.orElseThrow().get("aaaa").get("bbbb").get("cccc").asInt();
        Assertions.assertEquals(val, actual);
        System.out.println(newNode);
    }

    @Test
    public void whenThePathIsInLine_thenTheProertyIsCorrectlySet() {
        YmlService ymlReader = new YmlService();
        Optional<JsonNode> optNodeafter = ymlReader.read("storm/default-crawler.yml");
        JsonNode configuration = optNodeafter.orElseThrow();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        String pPath = CrawlerConfigurationEnum.HTTP_CONTENT_LIMIT.propertyPath;

        String newname = "newname";
        Optional<JsonNode> newNode = unit.setProperty(configuration, pPath, newname);
        Assertions.assertFalse(newNode.isEmpty());
        JsonNode innerNode = configuration.get("config");
        JsonNode node = innerNode.get("http.content.limit");
        Assertions.assertEquals(newname, node.asText());
    }
}
