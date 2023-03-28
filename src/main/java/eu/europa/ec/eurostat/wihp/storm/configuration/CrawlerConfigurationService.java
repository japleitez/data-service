package eu.europa.ec.eurostat.wihp.storm.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.domain.Acquisition;
import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.service.dto.DynamicConfigDTO;
import eu.europa.ec.eurostat.wihp.service.mapper.CrawlerMapper;
import eu.europa.ec.eurostat.wihp.storm.elastic.ElasticConfigurationService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class CrawlerConfigurationService {

    public static final String DEFAULT_YML = "storm/default-crawler.yml";
    protected static final String NAME_PATH = "name";
    private static final String STATUS_REPORT_ADDRESS = "config.topology.status.report.address";
    private static final String NIMBUS_SEEDS_PATH = "config.nimbus.seeds";
    private static final String SELENIUM_ADDRESS_PATH = "config.selenium.addresses";
    private static final String CHROME_OPTIONS_PREFS_PATH = "prefs";
    private static final String CHROME_OPTIONS_ARGS_PATH = "args";

    private static final String CHROME_OPTIONS_PREF_GEOLOCATION_KEY = "profile.managed_default_content_settings.geolocation";
    private static final String CHROME_OPTIONS_PREF_COOKIES_KEY = "profile.managed_default_content_settings.cookies";
    private static final String CHROME_OPTIONS_PREF_IMAGES_KEY = "profile.managed_default_content_settings.images";

    private static final String CHROME_OPTIONS_ARGS_MAXIMIZED_KEY = "--start-maximized";
    private static final String CHROME_OPTIONS_ARGS_LANGUAGE_KEY = "--lang=";
    private static final String CHROME_OPTIONS_ARGS_WINDOW_SIZE_KEY = "--window-size=";

    private final JsonNodeService jsonNodeService;
    private final YmlService ymlService;
    private final ConfigurationFileHolder configurationFileService;
    private final ElasticConfigurationService elasticConfigurationService;
    private final ApplicationProperties applicationProperties;
    private final CrawlerMapper crawlerMapper;

    public CrawlerConfigurationService(
        final JsonNodeService jsonNodeService,
        final YmlService ymlService,
        final ElasticConfigurationService elasticConfigurationService,
        final CrawlerMapper crawlerMapper,
        final ApplicationProperties applicationProperties
    ) {
        this.jsonNodeService = jsonNodeService;
        this.ymlService = ymlService;
        this.configurationFileService = new ConfigurationFileHolder();
        this.elasticConfigurationService = elasticConfigurationService;
        this.applicationProperties = applicationProperties;
        this.crawlerMapper = crawlerMapper;
    }

    public Optional<File> createAcquisitionConfig(final Acquisition acquisition) {
        return loadDefaults()
            .map(jsonNode -> apply(jsonNode, acquisition.getCrawler()))
            .map(jsonNode -> elasticConfigurationService.setup(jsonNode, acquisition))
            .map(jsonNode -> setTopologyName(jsonNode, acquisition.getTopologyName()))
            .map(jsonNode -> setTopologyReportAddress(jsonNode, acquisition.getId()))
            .map(this::setNimbusSeeds)
            .map(this::setSeleniumAddress)
            .map(jsonNode -> setSeleniumChromeOptionsPrefs(jsonNode, acquisition.getCrawler()))
            .map(jsonNode -> setSeleniumChromeOptionsArgs(jsonNode, acquisition.getCrawler()))
            .map(jsonNode -> save(acquisition.getId(), jsonNode))
            .map(Optional::get);
    }

    protected Optional<File> save(Long acquisitionId, JsonNode jsonNode) {
        return configurationFileService.save(acquisitionId, jsonNode, applicationProperties.getFluxPath(), applicationProperties.getFluxExt());
    }

    protected Optional<JsonNode> loadDefaults() {
        return ymlService.read(DEFAULT_YML);
    }

    protected JsonNode apply(final JsonNode configuration, final Crawler crawler) {
        validate(configuration, crawler);
        updateConfiguration(configuration, crawler);
        return configuration;
    }

    protected JsonNode setTopologyName(final JsonNode configuration, final String topologyName) {
        jsonNodeService.setProperty(configuration, CrawlerConfigurationService.NAME_PATH, topologyName);
        return configuration;
    }

    private JsonNode setTopologyReportAddress(final JsonNode configuration, final Long acquisitionId) {
        String address = applicationProperties.getTopologyReportAddress().replace(":id", String.valueOf(acquisitionId));
        jsonNodeService.setProperty(configuration, CrawlerConfigurationService.STATUS_REPORT_ADDRESS, address);
        return configuration;
    }

    protected JsonNode setNimbusSeeds(final JsonNode configuration) {
        jsonNodeService.setProperty(configuration, NIMBUS_SEEDS_PATH, applicationProperties.getNimbusSeeds());
        return configuration;
    }

    protected JsonNode setSeleniumAddress(final JsonNode configuration) {
        jsonNodeService.setProperty(configuration, SELENIUM_ADDRESS_PATH, applicationProperties.getSeleniumAddress());
        return configuration;
    }

    protected JsonNode setSeleniumChromeOptionsPrefs(final JsonNode configuration, Crawler crawler) {
        if (!Boolean.TRUE.equals(crawler.getDynamic())) {
            return configuration;
        }
        JsonNode chromeOptionsNode = getChromeOptionsNode(configuration);
        Map<String, Integer> chromeOptionsMapDefault = getChromeOptionsPrefsMap(chromeOptionsNode.get(CHROME_OPTIONS_PREFS_PATH));
        DynamicConfigDTO dynamicConfigDTO = crawlerMapper.map(crawler.getDynamicConfig());
        chromeOptionsMapDefault.put(CHROME_OPTIONS_PREF_GEOLOCATION_KEY, dynamicConfigDTO.getAllowGeolocation().getValue());
        chromeOptionsMapDefault.put(CHROME_OPTIONS_PREF_COOKIES_KEY, dynamicConfigDTO.getAllowCookies().getValue());
        chromeOptionsMapDefault.put(CHROME_OPTIONS_PREF_IMAGES_KEY, dynamicConfigDTO.getLoadImages().getValue());
        jsonNodeService.setProperty(chromeOptionsNode, CHROME_OPTIONS_PREFS_PATH, chromeOptionsMapDefault);
        return configuration;
    }

    protected JsonNode setSeleniumChromeOptionsArgs(final JsonNode configuration, final Crawler crawler) {
        if (!Boolean.TRUE.equals(crawler.getDynamic())) {
            return configuration;
        }
        JsonNode chromeOptionsNode = getChromeOptionsNode(configuration);
        List<String> chromeOptionsArgsDefault = getChromeOptionsArgsArray(chromeOptionsNode.get(CHROME_OPTIONS_ARGS_PATH));
        List<String> chromeOptionsArgsList = new ArrayList<>();
        DynamicConfigDTO dynamicConfigDTO = crawlerMapper.map(crawler.getDynamicConfig());
        chromeOptionsArgsDefault.forEach(option -> addOptionsArgsOption(chromeOptionsArgsList, option, dynamicConfigDTO));
        jsonNodeService.setProperty(chromeOptionsNode, CHROME_OPTIONS_ARGS_PATH, chromeOptionsArgsList);
        return configuration;
    }

    private void addOptionsArgsOption(final List<String> chromeOptionsArgsList, final String option, final DynamicConfigDTO dynamicConfigDTO) {
        if (option.contains(CHROME_OPTIONS_ARGS_MAXIMIZED_KEY)) {
            if (dynamicConfigDTO.isStartMaximized()) {
                chromeOptionsArgsList.add(CHROME_OPTIONS_ARGS_MAXIMIZED_KEY);
            }
        } else if (option.contains(CHROME_OPTIONS_ARGS_LANGUAGE_KEY)) {
            chromeOptionsArgsList.add(CHROME_OPTIONS_ARGS_LANGUAGE_KEY + dynamicConfigDTO.getLanguage());
        } else if (option.contains(CHROME_OPTIONS_ARGS_WINDOW_SIZE_KEY)) {
            chromeOptionsArgsList.add(CHROME_OPTIONS_ARGS_WINDOW_SIZE_KEY + dynamicConfigDTO.getWindowSize());
        } else {
            chromeOptionsArgsList.add(option);
        }
    }

    protected Map<String, Integer> getChromeOptionsPrefsMap(final JsonNode configuration) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Integer> map = mapper.convertValue(configuration, Map.class);
        return Objects.isNull(map) ? new HashMap<>() : map;
    }

    protected List<String> getChromeOptionsArgsArray(final JsonNode configuration) {
        ObjectMapper mapper = new ObjectMapper();
        List<String> list = mapper.convertValue(configuration, ArrayList.class);
        return Objects.isNull(list) ? new ArrayList<>() : list;
    }

    protected JsonNode getChromeOptionsNode(final JsonNode configuration) {
        return configuration.get("config").get("selenium.capabilities").get("goog:chromeOptions");
    }

    private void updateConfiguration(JsonNode configuration, Crawler crawler) {
        for (CrawlerConfigurationEnum property : CrawlerConfigurationEnum.values()) {
            jsonNodeService.setProperty(configuration, property.propertyPath, property.getValue(crawler));
        }
    }

    private void validate(final JsonNode configuration, final Crawler crawler) {
        Objects.requireNonNull(configuration);
        Objects.requireNonNull(crawler);

        if (configuration.isEmpty()) {
            throw new IllegalArgumentException(String.format("Property %s cannot be empty", configuration.asText()));
        }
    }
}
