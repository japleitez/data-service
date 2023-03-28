package eu.europa.ec.eurostat.wihp.storm.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.domain.ParseFilter;
import eu.europa.ec.eurostat.wihp.domain.ParserFilter;
import eu.europa.ec.eurostat.wihp.domain.UrlFilter;
import eu.europa.ec.eurostat.wihp.util.JsonNodeUtils;
import eu.europa.ec.eurostat.wihp.util.ResourcesUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LoadFiltersService {

    private static final String URL_FILTERS_CLASS = "com.digitalpebble.stormcrawler.filtering.URLFilters";
    private static final String PARSE_FILTERS_CLASS = "com.digitalpebble.stormcrawler.parse.ParseFilters";
    private static final String PARSE_FILTERS_CONFIG_FILE = "config.parsefilters.config.file";
    private static final String URL_FILTERS_CONFIG_FILE = "config.urlfilters.config.file";
    private static final String JSON_FILE_EXTENSION = ".json";

    private final ObjectMapper mapper = new ObjectMapper();
    private final JsonNodeService jsonNodeService;
    private final List<String> parseFilters;
    private final List<String> urlFilters;
    private final String wihpUrlFilterJson;
    private final String wihpUrlFilterClass;

    private final String wihpParseFilterJson;
    private final String wihpParseFilterClass;

    public LoadFiltersService(JsonNodeService jsonNodeService, ApplicationProperties applicationProperties) {
        this.jsonNodeService = jsonNodeService;
        this.parseFilters = applicationProperties.getParseFilterClasses();
        this.urlFilters = applicationProperties.getUrlFilterClasses();
        ResourcesUtils resources = new ResourcesUtils();

        this.wihpUrlFilterJson = resources.readFileAsString(applicationProperties.getWihpUrlFilterJsonTemplate());
        this.wihpUrlFilterClass = applicationProperties.getWihpUrlFilterClass();

        this.wihpParseFilterJson = resources.readFileAsString(applicationProperties.getWihpParseFilterJsonTemplate());
        this.wihpParseFilterClass = applicationProperties.getWihpParseFilterClass();
    }

    public Optional<JsonNode> loadParseFilters(Crawler crawler) {
        return getJsonNode(crawler, parseFilters, PARSE_FILTERS_CLASS);
    }

    public Optional<JsonNode> loadUrlFilters(Crawler crawler) {
        return getJsonNode(crawler, urlFilters, URL_FILTERS_CLASS);
    }

    public Optional<JsonNode> loadWihpUrlFilters(Crawler crawler) {
        ObjectNode mainNode = (ObjectNode) getJsonNode();
        ArrayNode arrayNode = mainNode.putArray(wihpUrlFilterClass);

        crawler.getUrlFilters().stream()
            .map(this::getWihpUrlFilterJsonNode)
            .forEach(arrayNode::add);

        return Optional.of(mainNode);
    }

    public Optional<JsonNode> loadWihpParseFilters(Crawler crawler) {
        ObjectNode mainNode = (ObjectNode) getJsonNode();
        ArrayNode arrayNode = mainNode.putArray(wihpParseFilterClass);

        crawler.getParseFilters().stream()
            .map(this::getWihpParseFilterJsonNode)
            .forEach(arrayNode::add);

        return Optional.of(mainNode);
    }

    private JsonNode getWihpUrlFilterJsonNode(UrlFilter urlFilter) {
        return JsonNodeUtils.createJsonNode(new WihpUrlFiltersWrapper(urlFilter));
    }

    private JsonNode getWihpParseFilterJsonNode(ParseFilter parseFilter) {
        return JsonNodeUtils.createJsonNode(new WihpParseFiltersWrapper(parseFilter));
    }

    private Optional<JsonNode> getJsonNode(Crawler crawler, List<String> filters, String urlFiltersClass) {
        ObjectNode mainNode = (ObjectNode) getJsonNode();
        ArrayNode arrayNode = mainNode.putArray(urlFiltersClass);
        filters
            .stream()
            .map(filter -> getFilterJsonNode(crawler, filter))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(arrayNode::add);

        return Optional.of(mainNode);
    }

    protected Optional<JsonNode> getFilterJsonNode(Crawler crawler, String className) {
        if (className.equals(wihpUrlFilterClass) && containWihpUrlFilter(crawler) ) {
            return getUrlWihpUrlFilter();
        }

        if (className.equals(wihpParseFilterClass) && containWihpParseFilter(crawler) ) {
            return getParseWihpUrlFilter();
        }

        return crawler.getParserFilters().stream().filter(f -> f.getClassName().equals(className)).findFirst().map(this::getJsonNode);
    }

    private boolean containWihpUrlFilter(Crawler crawler){
        return null != crawler.getUrlFilters() && !crawler.getUrlFilters().isEmpty();
    }

    private boolean containWihpParseFilter(Crawler crawler){
        return null != crawler.getParseFilters() && !crawler.getParseFilters().isEmpty();
    }

    private Optional<JsonNode> getUrlWihpUrlFilter() {
        return JsonNodeUtils.createJsonNode(wihpUrlFilterJson);
    }

    private Optional<JsonNode> getParseWihpUrlFilter() {
        return JsonNodeUtils.createJsonNode(wihpParseFilterJson);
    }


    private JsonNode getJsonNode(ParserFilter c) {
        JsonNode node = getJsonNode();
        setFilter(c, (ObjectNode) node);
        return node;
    }

    private void setFilter(ParserFilter pf, ObjectNode node) {
        if (!pf.getClassName().isBlank()) {
            node.put("class", pf.getClassName());
        }
        if (!pf.getName().isBlank()) {
            node.put("name", pf.getName());
        }
        if (pf.getParams() != null) {
            node.putPOJO("params", pf.getParams());
        }
    }

    private JsonNode getJsonNode() {
        return mapper.createObjectNode();
    }

    public Optional<JsonNode> applyParseFiltersConfigFile(JsonNode configuration, UUID workflowId, Long acquisitionId) {
        String value = "es-parsefilters_" + workflowId + "_" + acquisitionId + JSON_FILE_EXTENSION;
        return jsonNodeService.setProperty(configuration, PARSE_FILTERS_CONFIG_FILE, value);
    }

    public Optional<JsonNode> applyUrlFiltersConfigFile(JsonNode configuration, UUID workflowId, Long acquisitionId) {
        String value = "es-urlfilters_" + workflowId + "_" + acquisitionId + JSON_FILE_EXTENSION;
        return jsonNodeService.setProperty(configuration, URL_FILTERS_CONFIG_FILE, value);
    }

}
