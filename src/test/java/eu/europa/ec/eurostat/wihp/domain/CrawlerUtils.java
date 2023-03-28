package eu.europa.ec.eurostat.wihp.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.service.dto.DynamicConfigDTO;
import eu.europa.ec.eurostat.wihp.util.JsonNodeUtils;

public class CrawlerUtils {
    private static final Integer DEFAULT_FETCH_INTERVAL = 1440;
    private static final Integer DEFAULT_FETCH_INTERVAL_ERROR = 44640;
    private static final Integer DEFAULT_FETCH_INTERVAL_FETCH_ERROR = 120;
    private static final Integer DEFAULT_HTTP_CONTENT_LIMIT = 15000000;
    private static final Integer DEFAULT_MAX_EMIT_OUT_LINKS_PER_PAGE = -1;
    private static final Boolean DEFAULT_EXTRACTOR_NO_TEXT = false;
    private static final Boolean DEFAULT_EMIT_OUT_LINKS = true;
    private static final String DEFAULT_EXTRACTOR_TEXT_EXCLUDE_TAGS = "STYLE, SCRIPT";
    private static final String DEFAULT_EXTRACTOR_TEXT_EXCLUDE_PATTERN = "DIV[id=\"maincontent\"], DIV[itemprop=\"articleBody\"], ARTICLE";
    private static final Long DEFAULT_SOURCE_ID = 1L;
    private static final String DEFAULT_SOURCE_NAME = "eurostat";
    private static final String DEFAULT_SOURCE_URL = "https://ec.europa.eu/eurostat";
    private static final Long DEFAULT_FILTER_ID = 1L;
    private static final String DEFAULT_FILTER_CLASSNAME = "default_classname";
    private static final String DEFAULT_FILTER_NAME = "default_name";

    public static Crawler createCrawler() {
        Crawler crawler = new Crawler()
            .name("Crawler-Test")
            .fetchInterval(DEFAULT_FETCH_INTERVAL)
            .fetchIntervalWhenError(DEFAULT_FETCH_INTERVAL_ERROR)
            .fetchIntervalWhenFetchError(DEFAULT_FETCH_INTERVAL_FETCH_ERROR)
            .extractorNoText(DEFAULT_EXTRACTOR_NO_TEXT)
            .extractorTextExcludeTags(DEFAULT_EXTRACTOR_TEXT_EXCLUDE_TAGS)
            .extractorTextIncludePattern(DEFAULT_EXTRACTOR_TEXT_EXCLUDE_PATTERN)
            .httpContentLimit(DEFAULT_HTTP_CONTENT_LIMIT)
            .emitOutLinks(DEFAULT_EMIT_OUT_LINKS)
            .dynamic(false)
            .dynamicConfig(mapToJsonNode(new DynamicConfigDTO()))
            .maxEmitOutLinksPerPage(DEFAULT_MAX_EMIT_OUT_LINKS_PER_PAGE);
        // Add required entity
        Source source = new Source().id(DEFAULT_SOURCE_ID).name(DEFAULT_SOURCE_NAME).url(DEFAULT_SOURCE_URL);
        crawler.addSource(source);
        return crawler;
    }

    public static JsonNode mapToJsonNode(DynamicConfigDTO value) {
        return JsonNodeUtils.createJsonNode(value);
    }

    public static DynamicConfigDTO mapToDynamicConfigDTO(JsonNode jsonNode) {
        try {
            return JsonNodeUtils.getObject(jsonNode, DynamicConfigDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
