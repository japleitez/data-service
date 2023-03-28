package eu.europa.ec.eurostat.wihp.storm.elastic;

import static eu.europa.ec.eurostat.wihp.storm.elastic.ElasticConfigurationEnum.*;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.IntegrationTest;
import java.util.UUID;

import eu.europa.ec.eurostat.wihp.storm.configuration.YmlService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

@IntegrationTest
@WithMockUser
public class ElasticConfigurationServiceIT {

    static JsonNode configuration = null;

    static final UUID DEFAULT_WORKFLOW_ID = UUID.randomUUID();
    static final Long DEFAULT_ACQUISITION_ID = 1L;

    static final String DEFAULT_CONFIG_INDEX_NAME = IndexService.buildIndexName(CONFIG.prefix, DEFAULT_WORKFLOW_ID, DEFAULT_ACQUISITION_ID);
    static final String DEFAULT_CONTENT_INDEX_NAME = IndexService.buildIndexName(
        CONTENT.prefix,
        DEFAULT_WORKFLOW_ID,
        DEFAULT_ACQUISITION_ID
    );
    static final String DEFAULT_METRICS_INDEX_NAME = IndexService.buildIndexName(
        METRICS.prefix,
        DEFAULT_WORKFLOW_ID,
        DEFAULT_ACQUISITION_ID
    );
    static final String DEFAULT_STATUS_INDEX_NAME = IndexService.buildIndexName(STATUS.prefix, DEFAULT_WORKFLOW_ID, DEFAULT_ACQUISITION_ID);

    @Autowired
    ElasticConfigurationService service;

    @BeforeAll
    public static void setup() {
        YmlService ymlReader = new YmlService();
        configuration = ymlReader.read("storm/default-config.yml").get();
    }

    @Test
    public void checkUpdatingOfConfigParameter() {
        Assertions.assertNotNull(configuration);
        service.updateConfigIndexName(configuration, DEFAULT_WORKFLOW_ID, DEFAULT_ACQUISITION_ID);
        Assertions.assertEquals(DEFAULT_CONFIG_INDEX_NAME, configuration.get("config").get("es.config.index.name").asText());
    }

    @Test
    public void checkUpdatingOfContentParameter() {
        Assertions.assertNotNull(configuration);
        service.updateContentIndexName(configuration, DEFAULT_WORKFLOW_ID, DEFAULT_ACQUISITION_ID);
        Assertions.assertEquals(DEFAULT_CONTENT_INDEX_NAME, configuration.get("config").get("es.indexer.index.name").asText());
    }

    @Test
    public void checkUpdatingOfMetricsParameter() {
        Assertions.assertNotNull(configuration);
        service.updateMetricsIndexName(configuration, DEFAULT_WORKFLOW_ID, DEFAULT_ACQUISITION_ID);
        Assertions.assertEquals(DEFAULT_METRICS_INDEX_NAME, configuration.get("config").get("es.metrics.index.name").asText());
    }

    @Test
    public void checkUpdatingOfStatusParameter() {
        Assertions.assertNotNull(configuration);
        service.updateStatusIndexName(configuration, DEFAULT_WORKFLOW_ID, DEFAULT_ACQUISITION_ID);
        Assertions.assertEquals(DEFAULT_STATUS_INDEX_NAME, configuration.get("config").get("es.status.index.name").asText());
    }
}
