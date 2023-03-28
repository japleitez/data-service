package eu.europa.ec.eurostat.wihp.storm.elastic;

import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.storm.elastic.ElasticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
@WithMockUser
public class ElasticServiceIT {

    private static final String DEFAULT_IDX = "workflow-test";
    private static final String DEFAULT_SOURCE =
        "{\"settings\":{\"index\":{\"number_of_shards\":10,\"number_of_replicas\":0,\"refresh_interval\":\"5s\"}},\"mappings\":{\"dynamic_templates\":[{\"metadata\":{\"path_match\":\"metadata.*\",\"match_mapping_type\":\"string\",\"mapping\":{\"type\":\"keyword\"}}}],\"_source\":{\"enabled\":true},\"properties\":{\"key\":{\"type\":\"keyword\",\"index\":true},\"nextFetchDate\":{\"type\":\"date\",\"format\":\"date_optional_time\"},\"status\":{\"type\":\"keyword\"},\"url\":{\"type\":\"keyword\"}}}}";

    @Autowired
    ElasticService service;

    @BeforeEach
    public void reset() {
        service.deleteIndexIfExists(DEFAULT_IDX);
    }

    @Test
    public void whenElasticIsRunning_thenClientConnects() {
        assertTrue(service.isConnected());
    }

    @Test
    public void clientCanAddIndex() {
        assertTrue(service.createIndex(DEFAULT_IDX, DEFAULT_SOURCE));
        assertTrue(service.indexExists(DEFAULT_IDX));
    }
}
