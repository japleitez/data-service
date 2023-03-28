package eu.europa.ec.eurostat.wihp.storm.configuration;

import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.util.EmailUtils;
import eu.europa.ec.eurostat.wihp.util.UrlUtils;
import java.io.IOException;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class YmlReaderTest {

    YmlService ymlReader = new YmlService();

    @Test
    public void whenYmlFileIsNotPresentThen_IsPresent_False_Test() {
        assertFalse(ymlReader.isFilePresent("aaa").isPresent());
    }

    @Test
    public void whenReadYamlAndFileNotPresent_return_EmptyOptional_JsonNode() throws IOException {
        assertTrue(ymlReader.read("aa").isEmpty());
    }

    @Test
    public void whenReadYamlAndFilePresent_return_NonEmptyOptional_JsonNode() throws IOException {
        assertFalse(ymlReader.read("storm/default-crawler.yml").isEmpty());
    }

    @Test
    public void whenReadYamlAndFileWrong_ThrowException() {
        assertTrue(ymlReader.read("storm/default-wrong.yml").isEmpty());
    }

    @Test
    public void whenReadValidYaml_JsonNode_contains_ExpectedFields() throws IOException {
        Optional<JsonNode> optNode = ymlReader.read("storm/default-crawler.yml");
        JsonNode node = optNode.get();
        assertEquals("33", node.get("config").get("topology.workers").toPrettyString());
        assertEquals("111", node.get("config").get("topology.message.timeout.secs").toPrettyString());
    }

    @Test
    public void whenReadValidHttpAgent_thenExpectedFields() throws IOException {
        Optional<JsonNode> optNode = ymlReader.read("storm/default-crawler.yml");
        JsonNode node = optNode.get();

        String name = node.get("config").get("http.agent.name").asText();
        String version = node.get("config").get("http.agent.version").asText();
        String description = node.get("config").get("http.agent.description").asText();
        String url = node.get("config").get("http.agent.url").asText();
        String email = node.get("config").get("http.agent.email").asText();

        assertNotNull(name);
        assertFalse(StringUtils.isEmpty(name));
        assertTrue(name.length() < 255);

        assertNotNull(description);
        assertFalse(StringUtils.isEmpty(name));
        assertTrue(description.length() < 255);

        assertNotNull(url);
        assertFalse(StringUtils.isEmpty(url));
        assertTrue(UrlUtils.isValidUrl(url));

        assertNotNull(email);
        assertFalse(StringUtils.isEmpty(email));
        assertTrue(EmailUtils.isValidEmailAddress(email));

        assertNotNull(version);
        assertFalse(StringUtils.isEmpty(version));
    }
}
