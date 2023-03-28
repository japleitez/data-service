package eu.europa.ec.eurostat.wihp.storm.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.domain.ParseFilter;
import eu.europa.ec.eurostat.wihp.domain.ParserFilter;
import eu.europa.ec.eurostat.wihp.domain.UrlFilter;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class LoadFiltersServiceIT {

    static long count = 0;
    private static Crawler crawler;
    private LoadFiltersService unit;

    @Autowired
    private ApplicationProperties applicationProperties;

    @BeforeAll
    static void initCrawler() {
        crawler = getCrawler();
    }

    public static Crawler getCrawler() {
        Crawler crawler = new Crawler();
        getFilter(crawler, "urlfilters.json", "com.digitalpebble.stormcrawler.filtering.URLFilters");
        getFilter(crawler, "parsefilters.json", "com.digitalpebble.stormcrawler.parse.ParseFilters");
        getWihpUrlFilter(crawler, "wihpUrlFilter.json", "eu.europa.ec.eurostat.wihp.filters.url.WIHPFilters");
        getWihpParseFilter(crawler, "wihpParseFilter.json", "eu.europa.ec.eurostat.wihp.filters.parse.WIHPParseFilters");
        return crawler;
    }

    private static void getWihpUrlFilter(Crawler crawler, String name, String mainClass) {
        JsonNode arrayNode = getJsonArrayNode(name, mainClass);
        populateWihpUrlFilter(crawler, arrayNode);
    }

    private static void getWihpParseFilter(Crawler crawler, String name, String mainClass) {
        JsonNode arrayNode = getJsonArrayNode(name, mainClass);
        populateWihpParseFilter(crawler, arrayNode);
    }

    private static void getFilter(Crawler crawler, String name, String mainClass) {
        JsonNode arrayNode = getJsonArrayNode(name, mainClass);
        populateFilter(crawler, arrayNode);
    }

    private static JsonNode getJsonArrayNode(String name, String mainClass) {
        JsonNode node = getJsonNode(name);
        return node.get(mainClass);
    }

    private static JsonNode getJsonNodeFromYml(String name) throws IOException {
        File content = new File(Objects.requireNonNull(ClassOrderer.ClassName.class.getClassLoader().getResource(name)).getFile());
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readTree(content);
    }

    static JsonNode getJsonNode(String name) {
        File content = new File(Objects.requireNonNull(ClassOrderer.ClassName.class.getClassLoader().getResource(name)).getFile());
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return NullNode.instance;
    }

    private static void populateFilter(Crawler crawler, JsonNode arrayNode) {
        for (JsonNode n : arrayNode) {
            System.out.println(n.get("class"));
            crawler.addParserFilter(
                new ParserFilter()
                    .id(count++)
                    .className(n.get("class").asText())
                    .name(n.get("name").asText())
                    .crawler(new Crawler().id(1L))
                    .params(n.get("params"))
            );
        }
    }

    private static void populateWihpUrlFilter(Crawler crawler, JsonNode arrayNode) {
        for (JsonNode n : arrayNode) {
            crawler.addUrlFilter(UrlFilter.builder().filterId(n.get("class").asText()).configuration(n.get("configuration")).build());
        }
    }

    private static void populateWihpParseFilter(Crawler crawler, JsonNode arrayNode) {
        for (JsonNode n : arrayNode) {
            crawler.addParseFilter(
                ParseFilter
                    .builder()
                    .filterId(n.get("class").asText())
                    .configuration(n.get("configuration"))
                    .crawler(new Crawler().id(1L))
                    .build()
            );
        }
    }

    @BeforeEach
    public void initUnit() {
        unit = new LoadFiltersService(new JsonNodeService(), applicationProperties);
    }

    @Test
    public void filterStreamTwiceFails() {
        JsonNode node = unit.loadParseFilters(crawler).orElseThrow();
        assertEquals(
            "com.digitalpebble.stormcrawler.parse.filter.XPathFilter",
            node.get("com.digitalpebble.stormcrawler.parse.ParseFilters").get(0).get("class").asText()
        );
        node = unit.loadParseFilters(crawler).orElseThrow();
        assertEquals(
            "com.digitalpebble.stormcrawler.parse.filter.XPathFilter",
            node.get("com.digitalpebble.stormcrawler.parse.ParseFilters").get(0).get("class").asText()
        );
        //Should not throw java.lang.IllegalStateException: stream has already been operated upon or closed
        //A Stream should be operated on (invoking an intermediate or terminal stream operation) only once.
        //A Stream implementation may throw IllegalStateException if it detects that the Stream is being reused.
    }

    @Test
    public void whenLoadParserFilters_thenReturnOptionalArrayOfJsonNode() {
        JsonNode node = unit.loadParseFilters(crawler).orElseThrow();
        JsonNode expectedNode = getJsonNode("parsefilters.json");
        Assertions.assertEquals(expectedNode.asText(), node.asText());
    }

    @Test
    public void whenLoadURLFilters_thenReturnOptionalArrayOfJsonNode() {
        JsonNode node = unit.loadUrlFilters(crawler).orElseThrow();
        JsonNode expectedNode = getJsonNode("urlfilters.json");
        Assertions.assertEquals(expectedNode.asText(), node.asText());
    }

    @Test
    public void whenLoadWihpUrlFilters_thenReturnOptionalArrayOfJsonNode() {
        JsonNode node = unit.loadWihpUrlFilters(crawler).orElseThrow();
        JsonNode expectedNode = getJsonNode("wihpUrlFilter.json");
        Assertions.assertEquals(expectedNode.toString(), node.toString());
    }

    @Test
    public void whenLoadWihpParseFilters_thenReturnOptionalArrayOfJsonNode() {
        JsonNode node = unit.loadWihpParseFilters(crawler).orElseThrow();
        JsonNode expectedNode = getJsonNode("wihpParseFilter.json");
        Assertions.assertEquals(expectedNode.toString(), node.toString());
    }

    @Test
    public void whenGivenFilter_thenReturnOptionalJsonNode() {
        String className = "com.digitalpebble.stormcrawler.parse.filter.XPathFilter";
        Optional<JsonNode> filter = unit.getFilterJsonNode(crawler, className);
        Assertions.assertFalse(filter.isEmpty());
        Assertions.assertEquals(className, filter.get().get("class").asText());
    }

    @Test
    public void getFilterJsonNode_returnWithUrlFilter() {
        String className = "eu.europa.ec.eurostat.wihp.filters.url.WIHPFilters";
        Optional<JsonNode> filter = unit.getFilterJsonNode(crawler, className);
        Assertions.assertFalse(filter.isEmpty());
        Assertions.assertEquals(className, filter.get().get("class").asText());
        Assertions.assertEquals("WihpUrlFilter.json", filter.get().get("params").get("document").asText());
    }

    @Test
    public void getFilterJsonNode_notReturnsUrlFilter() {
        String className = "eu.europa.ec.eurostat.wihp.filters.url.WIHPFilters";
        Crawler crawler_noWHIP = getCrawler();
        crawler_noWHIP.setUrlFilters(null);
        Optional<JsonNode> filter = unit.getFilterJsonNode(crawler_noWHIP, className);
        Assertions.assertTrue(filter.isEmpty());
    }

    @Test
    public void getFilterJsonNode_returnWithParseFilter() {
        String className = "eu.europa.ec.eurostat.wihp.filters.parse.WIHPParseFilters";
        Optional<JsonNode> filter = unit.getFilterJsonNode(crawler, className);
        Assertions.assertFalse(filter.isEmpty());
        Assertions.assertEquals(className, filter.get().get("class").asText());
        Assertions.assertEquals("WihpParseFilter.json", filter.get().get("params").get("document").asText());
    }

    @Test
    public void getFilterJsonNode_notReturnsParseFilter() {
        String className = "eu.europa.ec.eurostat.wihp.filters.parse.WIHPParseFilters";
        Crawler crawler_noWHIP = getCrawler();
        crawler_noWHIP.setParseFilters(null);
        Optional<JsonNode> filter = unit.getFilterJsonNode(crawler_noWHIP, className);
        Assertions.assertTrue(filter.isEmpty());
    }

    @Test
    public void whenCrawlerExistsButClassPathNotExisting_thenError() {
        String className = "PleaseDoNotBeThere";
        Optional<JsonNode> filter = unit.getFilterJsonNode(crawler, className);
        Assertions.assertTrue(filter.isEmpty());
    }

    @Test
    public void whenApplyParseFilterConfigFileValue_thenValueIsApplied() throws IOException {
        JsonNode configuration = getJsonNodeFromYml("storm/default-crawler.yml");
        UUID workflowId = UUID.randomUUID();
        Long acquisitionId = 1L;
        final String expected = "es-parsefilters_" + workflowId + "_" + acquisitionId + ".json";

        JsonNode result = unit.applyParseFiltersConfigFile(configuration, workflowId, acquisitionId).orElseThrow();

        Assertions.assertEquals(expected, result.get("config").get("parsefilters.config.file").asText());
    }

    @Test
    public void whenApplyUrlFilterConfigFileValue_thenValueIsApplied() throws IOException {
        JsonNode configuration = getJsonNodeFromYml("storm/default-crawler.yml");
        UUID workflowId = UUID.randomUUID();
        Long acquisitionId = 1L;
        String expected = "es-urlfilters_" + workflowId + "_" + acquisitionId + ".json";
        JsonNode result = unit.applyUrlFiltersConfigFile(configuration, workflowId, acquisitionId).orElseThrow();
        Assertions.assertEquals(expected, result.get("config").get("urlfilters.config.file").asText());
    }

    @Test
    public void getFilterJsonNode_test() {
        JsonNode parseNode = unit.getFilterJsonNode(crawler, "com.digitalpebble.stormcrawler.filtering.depth.MaxDepthFilter").orElseThrow();
        assertEquals("com.digitalpebble.stormcrawler.filtering.depth.MaxDepthFilter", parseNode.get("class").asText());

        JsonNode wihpNode = unit.getFilterJsonNode(crawler, "eu.europa.ec.eurostat.wihp.filters.url.WIHPFilters").orElseThrow();
        assertEquals("eu.europa.ec.eurostat.wihp.filters.url.WIHPFilters", wihpNode.get("class").asText());
        assertEquals("WihpUrlFilter.json", wihpNode.get("params").get("document").asText());
    }
}
