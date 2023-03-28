package eu.europa.ec.eurostat.wihp.storm.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.POJONode;
import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.domain.Acquisition;
import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.domain.CrawlerUtils;
import eu.europa.ec.eurostat.wihp.service.dto.DynamicConfigDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SeleniumOptionsEnum;
import eu.europa.ec.eurostat.wihp.service.mapper.CrawlerMapper;
import eu.europa.ec.eurostat.wihp.storm.elastic.ElasticConfigurationService;
import eu.europa.ec.eurostat.wihp.web.rest.AcquisitionResourceIT;
import eu.europa.ec.eurostat.wihp.web.rest.CrawlerResourceIT;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static eu.europa.ec.eurostat.wihp.storm.configuration.CrawlerConfigurationService.DEFAULT_YML;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CrawlerConfigurationServiceTest {

    private static final String DEFAULT_JSON_NODE_STRING = "{ \"name\": \"value\" }";
    private static final String DEFAULT_EXTRACTOR_TEXT_INCLUDE_PATTERN = "- DIV[id=\"maincontent\"]" + "- DIV[itemprop=\"articleBody\"]" + "- ARTICLE";
    private static final String DEFAULT_EXTRACTOR_TEXT_EXCLUDE_TAGS = "- STYLE" + "- SCRIPT";
    private final JsonNodeService jsonNodeService = new JsonNodeService();
    private final Acquisition acquisition = AcquisitionResourceIT.generateAcquisition(UUID.randomUUID());
    private final Crawler crawler = CrawlerResourceIT.createCrawlerEntity();
    @InjectMocks
    private CrawlerConfigurationService unit;
    @Mock
    private YmlService mockYmlService;
    @Mock
    private ElasticConfigurationService mockElasticConfigurationService;
    @Mock
    private ApplicationProperties mockApplicationProperties;

    private JsonNode configuration;

    Crawler createCrawlerEntity() {
        return new Crawler()
            .name("name")
            .fetchInterval(-1)
            .fetchIntervalWhenError(-1)
            .fetchIntervalWhenFetchError(-1)
            .extractorNoText(false)
            .extractorTextIncludePattern(DEFAULT_EXTRACTOR_TEXT_INCLUDE_PATTERN)
            .extractorTextExcludeTags(DEFAULT_EXTRACTOR_TEXT_EXCLUDE_TAGS)
            .httpContentLimit(-1)
            .emitOutLinks(false)
            .dynamic(false)
            .maxEmitOutLinksPerPage(-1);
    }

    @BeforeEach
    void init() {
        CrawlerMapper crawlerMapper =  Mappers.getMapper( CrawlerMapper.class );

        unit =
            new CrawlerConfigurationService(
                jsonNodeService,
                mockYmlService,
                mockElasticConfigurationService,
                crawlerMapper,
                mockApplicationProperties
            );
    }

    @Test
    public void whenConfigurationEmptyOrNull_thenThrowIllegalArgumentException() {
        configuration = new ObjectMapper().createObjectNode();
        Crawler crawler = createCrawlerEntity();
        Assert.assertThrows(IllegalArgumentException.class, () -> unit.apply(configuration, crawler));
    }

    @Test
    public void whenConfigurationEmptyOrNull_thenThrowNullPointerException() {
        configuration = null;
        Crawler crawler = null;
        Assert.assertThrows(NullPointerException.class, () -> unit.apply(configuration, crawler));
    }

    @Test
    public void whenCrawlerIsNull_thenThrowIllegalArgumentException() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        configuration = mapper.readTree(DEFAULT_JSON_NODE_STRING);

        Assert.assertThrows(NullPointerException.class, () -> unit.apply(configuration, null));
    }

    @Test
    public void whenCrawlerNameFound_thenSaveConfiguration() {
        JsonNode configuration = getConfiguration();
        Crawler crawlerEntity = createCrawlerEntity();

        Integer httpContentLimitValue = (Integer) CrawlerConfigurationEnum.HTTP_CONTENT_LIMIT.getValue(crawlerEntity);
        Integer fetchDefaulBefore = (Integer) CrawlerConfigurationEnum.FETCHINTERVAL_DEFAULT.getValue(crawlerEntity);
        Integer fetchFetchDefaulBefore = (Integer) CrawlerConfigurationEnum.FETCHINTERVAL_FETCH_ERROR.getValue(crawlerEntity);

        unit.apply(configuration, crawlerEntity);

        JsonNode innerNode = configuration.get("config");
        JsonNode node;

        node = innerNode.get(CrawlerConfigurationEnum.HTTP_CONTENT_LIMIT.propertyPath.substring(7));
        assertEquals(httpContentLimitValue, node.asInt());

        node = innerNode.get(CrawlerConfigurationEnum.FETCHINTERVAL_DEFAULT.propertyPath.substring(7));
        assertEquals(fetchDefaulBefore, node.asInt());

        node = innerNode.get(CrawlerConfigurationEnum.FETCHINTERVAL_FETCH_ERROR.propertyPath.substring(7));
        assertEquals(fetchFetchDefaulBefore, node.asInt());
    }

    @Test
    public void whenThePropertyIsAnArray_thenNodeHasMultipleElements() {
        JsonNode configuration = getConfiguration();
        Crawler crawlerEntity = createCrawlerEntity();

        unit.apply(configuration, crawlerEntity);

        JsonNode node;
        JsonNode innerNode = configuration.get("config");
        node = innerNode.get(CrawlerConfigurationEnum.TEXTEXTRACTOR_INCLUDE_PATTERN.propertyPath.substring(7));
        assertEquals(3, node.size());

        node = innerNode.get(CrawlerConfigurationEnum.TEXTEXTRACTOR_EXCLUDE_TAGS.propertyPath.substring(7));
        assertEquals(2, node.size());
    }

    @Test
    public void whenLoadDefaults_thenGetDefaultJsonNode() {
        when(mockYmlService.read(eq(DEFAULT_YML))).thenReturn(Optional.of(new POJONode("test")));

        Optional<JsonNode> jsonNode = unit.loadDefaults();

        assertTrue(jsonNode.isPresent());
        verify(mockYmlService).read(eq(DEFAULT_YML));
    }

    @Test
    public void whenLoadDefaultsApply_thenLoadDefaultsAndApply() throws IOException {
        JsonNode configuration = getConfiguration();
        acquisition.setCrawler(crawler);
        Long acquisitionId = 1L;
        acquisition.setId(acquisitionId);

        FileUtils.deleteDirectory(new File(".\\acquisition_" + acquisitionId));

        when(mockYmlService.read(eq(DEFAULT_YML))).thenReturn(Optional.of(configuration));
        when(mockElasticConfigurationService.setup(any(), any())).thenReturn(configuration);
        when(mockApplicationProperties.getTopologyReportAddress()).thenReturn("host.docker.internal:8081/api/acquisitions/:id/report");
        when(mockApplicationProperties.getSeleniumAddress()).thenReturn("seleniumAddress");
        when(mockApplicationProperties.getFluxPath()).thenReturn(ConfigurationFileHolderTest.PATH);
        when(mockApplicationProperties.getFluxExt()).thenReturn(ConfigurationFileHolderTest.EXT);
        Optional<File> result = unit.createAcquisitionConfig(acquisition);

        assertTrue(result.isPresent());
        verify(mockYmlService).read(eq(DEFAULT_YML));
        verify(mockElasticConfigurationService).setup(any(), any());
    }

    @Test
    public void whenSetName_nameShouldSet() {
        String topologyName = "name-123123";
        configuration = new ObjectMapper().createObjectNode();
        unit.setTopologyName(configuration, topologyName);
        assertEquals(topologyName, configuration.get(CrawlerConfigurationService.NAME_PATH).asText());
    }

    @Test
    public void whenSetNimbusSeeds_thenNimbusSeedsShouldSet() {
        final String expected1 = "nimbus.seeds1";
        final String expected2 = "nimbus.seeds2";
        when(mockApplicationProperties.getNimbusSeeds()).thenReturn(Arrays.asList(expected1, expected2));
        JsonNode configuration = getConfiguration();

        unit.setNimbusSeeds(configuration);

        final JsonNode config = configuration.get("config");
        assertNotNull(config);
        final JsonNode nimbusSeeds = config.get("nimbus.seeds");
        assertTrue(nimbusSeeds instanceof ArrayNode);
        assertEquals(expected1, nimbusSeeds.get(0).asText());
        assertEquals(expected2, nimbusSeeds.get(1).asText());
    }

    @Test
    public void whenSetSeleniumAddress_thenSeleniumAddressSet() {
        final String expected = "seleniumAddress";
        when(mockApplicationProperties.getSeleniumAddress()).thenReturn(expected);
        JsonNode configuration = getConfiguration();

        unit.setSeleniumAddress(configuration);

        final JsonNode config = configuration.get("config");
        assertNotNull(config);
        final JsonNode seleniumAddress = config.get("selenium.addresses");
        assertEquals(expected, seleniumAddress.asText());
    }

    @Test
    public void whenDynamicIsFalse_thenHttpProtocol() {
        JsonNode configuration = getConfiguration();
        Crawler crawlerEntity = createCrawlerEntity();

        unit.apply(configuration, crawlerEntity);

        JsonNode innerNode = configuration.get("config");
        JsonNode node;

        node = innerNode.get("http.protocol.implementation");
        assertEquals("com.digitalpebble.stormcrawler.protocol.okhttp.HttpProtocol", node.asText());

        node = innerNode.get("https.protocol.implementation");
        assertEquals("com.digitalpebble.stormcrawler.protocol.okhttp.HttpProtocol", node.asText());
    }

    @Test
    public void whenDynamicIsTrue_thenRemoteDriverProtocol() {
        JsonNode configuration = getConfiguration();
        Crawler crawlerEntity = createCrawlerEntity();
        crawlerEntity.setDynamic(true);

        unit.apply(configuration, crawlerEntity);

        JsonNode innerNode = configuration.get("config");
        JsonNode node;

        node = innerNode.get("http.protocol.implementation");
        assertEquals("com.digitalpebble.stormcrawler.protocol.selenium.RemoteDriverProtocol", node.asText());

        node = innerNode.get("https.protocol.implementation");
        assertEquals("com.digitalpebble.stormcrawler.protocol.selenium.RemoteDriverProtocol", node.asText());
    }

    @Test
    public void setSeleniumChromeOptionsArgs_when_NoDynamic_thenAllDefaultValues() {

        ObjectMapper mapper = new ObjectMapper();

        JsonNode loadedConfiguration = readYml(DEFAULT_YML);
        Crawler testCrawler = createCrawlerEntity();

        DynamicConfigDTO dynamicConfig = new DynamicConfigDTO();
        dynamicConfig.setWindowSize("1111,1111");
        dynamicConfig.setLanguage("jp-RU");
        dynamicConfig.setStartMaximized(false);
        dynamicConfig.setLoadImages(SeleniumOptionsEnum.ALLOW);
        dynamicConfig.setAllowCookies(SeleniumOptionsEnum.ALLOW);
        dynamicConfig.setAllowGeolocation(SeleniumOptionsEnum.ALLOW);

        testCrawler.setDynamicConfig(CrawlerUtils.mapToJsonNode(dynamicConfig));
        testCrawler.setDynamic(null);

        unit.setSeleniumChromeOptionsArgs(loadedConfiguration, testCrawler);
        unit.setSeleniumChromeOptionsPrefs(loadedConfiguration, testCrawler);

        List<String> chromeOptionsArgs = mapper.convertValue(loadedConfiguration.get("config").get("selenium.capabilities").get("goog:chromeOptions").get("args"), ArrayList.class);
        assertEquals(9, chromeOptionsArgs.size());
        assertTrue(chromeOptionsArgs.contains("--headless"));
        assertTrue(chromeOptionsArgs.contains("--lang=de"));
        assertTrue(chromeOptionsArgs.contains("--mute-audio"));
        assertTrue(chromeOptionsArgs.contains("--start-maximized"));
        assertTrue(chromeOptionsArgs.contains("--no-sandbox"));
        assertTrue(chromeOptionsArgs.contains("--window-size=1080,1920"));
        assertTrue(chromeOptionsArgs.contains("--disable-popup-blocking"));
        assertTrue(chromeOptionsArgs.contains("--disable-audio-output"));
        assertTrue(chromeOptionsArgs.get(8).contains("--user-agent=Web Intelligence Hub 0.1 The WIH is run by Eurostat"));

        Map<String, Integer> chromeOptionsPrefsMap = mapper.convertValue(loadedConfiguration.get("config").get("selenium.capabilities").get("goog:chromeOptions").get("prefs"), Map.class);
        assertEquals(2, chromeOptionsPrefsMap.get("profile.managed_default_content_settings.notifications"));
        assertEquals(2, chromeOptionsPrefsMap.get("profile.managed_default_content_settings.images"));
        assertEquals(2, chromeOptionsPrefsMap.get("profile.managed_default_content_settings.media_stream"));
        assertEquals(2, chromeOptionsPrefsMap.get("profile.managed_default_content_settings.cookies"));
        assertEquals(2, chromeOptionsPrefsMap.get("profile.managed_default_content_settings.plugins"));
        assertEquals(2, chromeOptionsPrefsMap.get("profile.managed_default_content_settings.geolocation"));
    }

    @Test
    public void setSeleniumChromeOptionsArgs_when_DynamicIsSet_thenUpdateValues() {

        String WINDOW_SIZE_NEW = "1111,1111";
        String LANGUAGE_NEW = "jp-RU";

        ObjectMapper mapper = new ObjectMapper();

        JsonNode loadedConfiguration = readYml(DEFAULT_YML);
        Crawler testCrawler = createCrawlerEntity();

        DynamicConfigDTO dynamicConfig = new DynamicConfigDTO();
        dynamicConfig.setWindowSize(WINDOW_SIZE_NEW);
        dynamicConfig.setLanguage(LANGUAGE_NEW);
        dynamicConfig.setStartMaximized(false);

        testCrawler.setDynamicConfig(CrawlerUtils.mapToJsonNode(dynamicConfig));
        testCrawler.setDynamic(true);

        unit.setSeleniumChromeOptionsArgs(loadedConfiguration, testCrawler);

        List<String> chromeOptionsArgs = mapper.convertValue(loadedConfiguration.get("config").get("selenium.capabilities").get("goog:chromeOptions").get("args"), ArrayList.class);
        assertEquals(8, chromeOptionsArgs.size());
        assertTrue(chromeOptionsArgs.contains("--headless"));
        assertTrue(chromeOptionsArgs.contains("--lang=" + LANGUAGE_NEW));
        assertTrue(chromeOptionsArgs.contains("--mute-audio"));
        assertFalse(chromeOptionsArgs.contains("--start-maximized"));
        assertTrue(chromeOptionsArgs.contains("--no-sandbox"));
        assertTrue(chromeOptionsArgs.contains("--window-size=" + WINDOW_SIZE_NEW));
        assertTrue(chromeOptionsArgs.contains("--disable-popup-blocking"));
        assertTrue(chromeOptionsArgs.contains("--disable-audio-output"));
        assertTrue(chromeOptionsArgs.get(7).contains("--user-agent=Web Intelligence Hub 0.1 The WIH is run by Eurostat"));
        assertTrue(chromeOptionsArgs.get(7).contains("ESTAT-WIH@ec.europa.eu"));
    }

    @Test
    public void setSeleniumChromeOptionsPrefs_when_DynamicIsSet_thenUpdateValues() {

        ObjectMapper mapper = new ObjectMapper();

        JsonNode loadedConfiguration = readYml(DEFAULT_YML);
        Crawler testCrawler = createCrawlerEntity();

        DynamicConfigDTO dynamicConfig = new DynamicConfigDTO();
        dynamicConfig.setLoadImages(SeleniumOptionsEnum.ALLOW);
        dynamicConfig.setAllowCookies(SeleniumOptionsEnum.ALLOW);
        dynamicConfig.setAllowGeolocation(SeleniumOptionsEnum.ALLOW);

        testCrawler.setDynamicConfig(CrawlerUtils.mapToJsonNode(dynamicConfig));
        testCrawler.setDynamic(true);

        unit.setSeleniumChromeOptionsPrefs(loadedConfiguration, testCrawler);

        Map<String, Integer> chromeOptionsPrefsMap = mapper.convertValue(loadedConfiguration.get("config").get("selenium.capabilities").get("goog:chromeOptions").get("prefs"), Map.class);
        assertEquals(2, chromeOptionsPrefsMap.get("profile.managed_default_content_settings.notifications"));
        assertEquals(1, chromeOptionsPrefsMap.get("profile.managed_default_content_settings.images"));
        assertEquals(2, chromeOptionsPrefsMap.get("profile.managed_default_content_settings.media_stream"));
        assertEquals(1, chromeOptionsPrefsMap.get("profile.managed_default_content_settings.cookies"));
        assertEquals(2, chromeOptionsPrefsMap.get("profile.managed_default_content_settings.plugins"));
        assertEquals(1, chromeOptionsPrefsMap.get("profile.managed_default_content_settings.geolocation"));
    }

    @Test
    public void setSeleniumChromeOptionsArgs_when_noDefaultValues() {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode loadedConfiguration = readYml("storm/default-crawler-no_chrom-options.yml");
        Crawler testCrawler = createCrawlerEntity();

        DynamicConfigDTO dynamicConfig = new DynamicConfigDTO();
        dynamicConfig.setWindowSize("1111,1111");
        dynamicConfig.setLanguage("jp-RU");
        dynamicConfig.setStartMaximized(false);
        dynamicConfig.setLoadImages(SeleniumOptionsEnum.ALLOW);
        dynamicConfig.setAllowCookies(SeleniumOptionsEnum.ALLOW);
        dynamicConfig.setAllowGeolocation(SeleniumOptionsEnum.ALLOW);

        testCrawler.setDynamicConfig(CrawlerUtils.mapToJsonNode(dynamicConfig));
        testCrawler.setDynamic(true);

        unit.setSeleniumChromeOptionsArgs(loadedConfiguration, testCrawler);
        unit.setSeleniumChromeOptionsPrefs(loadedConfiguration, testCrawler);

        List<String> chromeOptionsArgs = mapper.convertValue(loadedConfiguration.get("config").get("selenium.capabilities").get("goog:chromeOptions").get("args"), ArrayList.class);
        assertEquals(3, chromeOptionsArgs.size());
        assertTrue(chromeOptionsArgs.contains("--lang=jp-RU"));
        assertTrue(chromeOptionsArgs.contains("--disable-popup-blocking"));
        assertTrue(chromeOptionsArgs.get(2).contains("--user-agent=Web Intelligence Hub 0.1 The WIH is run by Eurostat"));

        Map<String, Integer> chromeOptionsPrefsMap = mapper.convertValue(loadedConfiguration.get("config").get("selenium.capabilities").get("goog:chromeOptions").get("prefs"), Map.class);
        assertEquals(3, chromeOptionsPrefsMap.size());
        assertEquals(1, chromeOptionsPrefsMap.get("profile.managed_default_content_settings.images"));
        assertEquals(1, chromeOptionsPrefsMap.get("profile.managed_default_content_settings.cookies"));
        assertEquals(1, chromeOptionsPrefsMap.get("profile.managed_default_content_settings.geolocation"));
    }

    private JsonNode readYml(String fileName) {
        YmlService ymlService = new YmlService();
        return ymlService.read(fileName).get();
    }

    private JsonNode getConfiguration() {
        YmlService ymlReader = new YmlService();
        Optional<JsonNode> optNodeBefore = ymlReader.read(DEFAULT_YML);
        return optNodeBefore.orElseThrow();
    }
}
