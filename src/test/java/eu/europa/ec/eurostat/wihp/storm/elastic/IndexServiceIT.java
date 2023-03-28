package eu.europa.ec.eurostat.wihp.storm.elastic;

import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.util.ResourcesUtils;
import org.elasticsearch.ElasticsearchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
@WithMockUser
public class IndexServiceIT {

    public static final String PARSE_FILTER_INDEX_ID = "es-parsefilters.json";
    public static final String URL_FILTER_INDEX_ID = "es-urlfilters.json";
    public static final String STORM_METRICS_TEMPLATE = "storm-metrics-template";
    public static final String WIHP_URL_FILTER_INDEX_ID = "WihpUrlFilter.json";
    public static final String WIHP_PARSE_FILTER_INDEX_ID = "WihpParseFilter.json";

    static final String CONFIG_KEY = "config";
    static final String CONTENT_KEY = "content";
    static final String STATUS_KEY = "status";
    private static final UUID DEFAULT_WORKFLOW_ID = UUID.randomUUID();
    private static final Long DEFAULT_ACQUISITION_ID = 1L;
    private static final String DEFAULT_LINK = "https://ec.europa.eu/eurostat/data/database";
    private static final String DEFAULT_PREFIX = "config";
    private static final String DEFAULT_FILE = "./elastic/demo.json";
    private static final String DEFAULT_PARSE_FILE = "./elastic/demo_parse_filters.json";
    private static final String DEFAULT_URL_FILE = "./elastic/demo_url_filters.json";
    private static final String WIHP_URL_FILTER_FILE = "elastic/WihpUrlFilter.json";
    private static final String WIHP_PARSE_FILTER_FILE = "elastic/WihpParseFilter.json";

    private static final String DEFAULT_INDEX = IndexService.buildIndexName(DEFAULT_PREFIX, DEFAULT_WORKFLOW_ID, DEFAULT_ACQUISITION_ID);

    private static final String DEFAULT_STATUS_INDEX = IndexService.buildIndexName(
        STATUS_KEY,
        DEFAULT_WORKFLOW_ID,
        DEFAULT_ACQUISITION_ID
    );
    private static final String DEFAULT_CONTENT_INDEX = IndexService.buildIndexName(
        CONTENT_KEY,
        DEFAULT_WORKFLOW_ID,
        DEFAULT_ACQUISITION_ID
    );
    private static final String DEFAULT_CONFIG_INDEX = IndexService.buildIndexName(
        CONFIG_KEY,
        DEFAULT_WORKFLOW_ID,
        DEFAULT_ACQUISITION_ID
    );

    @Autowired
    IndexService indexService;

    @Autowired
    ElasticService elasticService;

    ResourcesUtils resources = new ResourcesUtils();

    ApplicationProperties applicationProperties = new ApplicationProperties();

    @BeforeEach
    public void reset() {
        elasticService.deleteIndexIfExists(DEFAULT_INDEX);
        elasticService.deleteIndexIfExists(DEFAULT_STATUS_INDEX);
        elasticService.deleteIndexIfExists(DEFAULT_CONTENT_INDEX);
        elasticService.deleteIndexIfExists(DEFAULT_CONFIG_INDEX);
    }

    @Test
    public void checkIndexName() {
        String expected = DEFAULT_STATUS_INDEX + "_" + DEFAULT_WORKFLOW_ID + "_" + DEFAULT_ACQUISITION_ID;
        String actual = IndexService.buildIndexName(DEFAULT_STATUS_INDEX, DEFAULT_WORKFLOW_ID, DEFAULT_ACQUISITION_ID);
        assertEquals(expected, actual);
    }

    @Test
    public void checkStatusIndexIsCreated() {
        indexService.createStatusIndex(DEFAULT_WORKFLOW_ID, DEFAULT_ACQUISITION_ID);
        assertTrue(elasticService.indexExists(DEFAULT_STATUS_INDEX));
    }

    @Test
    public void checkContentIndexIsCreated() {
        indexService.createContentIndex(DEFAULT_WORKFLOW_ID, DEFAULT_ACQUISITION_ID);
        assertTrue(elasticService.indexExists(DEFAULT_CONTENT_INDEX));
    }

    @Test
    public void checkConfigIndexIsCreated() {
        indexService.createConfigIndex(DEFAULT_WORKFLOW_ID, DEFAULT_ACQUISITION_ID);
        assertTrue(elasticService.indexExists(DEFAULT_CONFIG_INDEX));
    }

    @Test
    public void checkMetricsTemplateIsCreated() {
        indexService.createMetricsTemplate();
        assertTrue(elasticService.templateExists(STORM_METRICS_TEMPLATE));
    }

    @Test
    public void setupIndices_test() {
        indexService.setupIndices(DEFAULT_WORKFLOW_ID, DEFAULT_ACQUISITION_ID);
        assertTrue(elasticService.indexExists(DEFAULT_STATUS_INDEX));
        assertTrue(elasticService.indexExists(DEFAULT_CONTENT_INDEX));
        assertTrue(elasticService.indexExists(DEFAULT_CONFIG_INDEX));
        assertTrue(elasticService.templateExists(STORM_METRICS_TEMPLATE));
    }

    @Test
    public void checkParseFilterIsCreated() throws IOException {
        createDefaultIndex();
        indexService.createParseFilter(
            DEFAULT_WORKFLOW_ID,
            DEFAULT_ACQUISITION_ID,
            FileUtils.readFileToString(resources.getFile(DEFAULT_PARSE_FILE), "utf-8")
        );
        String source = elasticService.getSourceForIndexAndId(DEFAULT_INDEX, PARSE_FILTER_INDEX_ID);
        assertNotNull(source);
    }

    @Test
    public void checkParseFilterIsCreated_exceptionTest() {
        createDefaultIndex();
        assertThrows(ElasticsearchException.class, () -> indexService.createParseFilter(
            DEFAULT_WORKFLOW_ID, DEFAULT_ACQUISITION_ID, "with Error"));
    }

    @Test
    public void checkUrlFilterIsCreated() throws IOException {
        createDefaultIndex();
        String parserFilter = indexService.createUrlFilter(
            DEFAULT_WORKFLOW_ID,
            DEFAULT_ACQUISITION_ID,
            FileUtils.readFileToString(resources.getFile(DEFAULT_URL_FILE), "utf-8")
        );
        assertEquals(URL_FILTER_INDEX_ID, parserFilter);

        String source = elasticService.getSourceForIndexAndId(DEFAULT_INDEX, URL_FILTER_INDEX_ID);
        assertNotNull(source);
    }

    @Test
    public void checkUrlFilterIsCreated_exceptionTest() {
        createDefaultIndex();
        assertThrows(ElasticsearchException.class, () -> indexService.createUrlFilter(
            DEFAULT_WORKFLOW_ID, DEFAULT_ACQUISITION_ID, "with Error"));
    }

    @Test
    public void createWihpUrlFilter_test() throws IOException {
        createDefaultIndex();
        String index = indexService.createWihpUrlFilter(
            DEFAULT_WORKFLOW_ID,
            DEFAULT_ACQUISITION_ID,
            FileUtils.readFileToString(resources.getFile(WIHP_URL_FILTER_FILE), "utf-8")
        );
        assertEquals(WIHP_URL_FILTER_INDEX_ID, index);
    }

    @Test
    public void createWihpUrlFilter_exceptionTest() {
        createDefaultIndex();
        assertThrows(ElasticsearchException.class, () -> indexService.createWihpUrlFilter(
            DEFAULT_WORKFLOW_ID, DEFAULT_ACQUISITION_ID, "with Error"));
    }

    @Test
    public void createWihpParseFilter_test() throws IOException {
        createDefaultIndex();
        String index = indexService.createWihpParseFilter(
            DEFAULT_WORKFLOW_ID,
            DEFAULT_ACQUISITION_ID,
            FileUtils.readFileToString(resources.getFile(WIHP_PARSE_FILTER_FILE), "utf-8")
        );
        assertEquals(WIHP_PARSE_FILTER_INDEX_ID, index);
    }

    @Test
    public void createWihpParseFilter_exceptionTest() {
        createDefaultIndex();
        assertThrows(ElasticsearchException.class, () -> indexService.createWihpParseFilter(
            DEFAULT_WORKFLOW_ID, DEFAULT_ACQUISITION_ID, "with Error"));
    }

    @Test
    public void checkStormSourceIsCreated() {

        String indexName = IndexService.buildIndexName(applicationProperties.getStatusIndexSuffix(), DEFAULT_WORKFLOW_ID, DEFAULT_ACQUISITION_ID);

        indexService.createIndex("demo", DEFAULT_WORKFLOW_ID, DEFAULT_ACQUISITION_ID, resources.getFile(DEFAULT_FILE));
        String stormSource = indexService.createStormSource(indexName, DEFAULT_LINK);
        assertNotNull(stormSource);
    }

    @Test
    public void createIndex_exceptionTest() {
        createDefaultIndex();
        assertThrows(ElasticsearchException.class, () -> indexService.createIndex(
            "demo", DEFAULT_WORKFLOW_ID, DEFAULT_ACQUISITION_ID, new File("with Error")));
    }

    private void createDefaultIndex() {
        String index = indexService.createIndex(
            DEFAULT_PREFIX,
            DEFAULT_WORKFLOW_ID,
            DEFAULT_ACQUISITION_ID,
            resources.getFile(DEFAULT_FILE)
        );
        assertNotNull(index);
    }


}
