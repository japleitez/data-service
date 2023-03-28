package eu.europa.ec.eurostat.wihp.storm.elastic;

import static eu.europa.ec.eurostat.wihp.storm.elastic.ElasticConfigurationEnum.*;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.domain.Acquisition;
import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.domain.Source;
import eu.europa.ec.eurostat.wihp.storm.configuration.JsonNodeService;
import eu.europa.ec.eurostat.wihp.storm.configuration.LoadFiltersService;
import eu.europa.ec.eurostat.wihp.util.PartitionList;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.stereotype.Service;

@Service
public class ElasticConfigurationService {

    public static final String ES_CONFIG_INDEX_ADDRESSES = "config.es.config.addresses";
    public static final String ES_CONTENT_INDEX_ADDRESSES = "config.es.indexer.addresses";
    public static final String ES_METRICS_INDEX_ADDRESSES = "config.es.metrics.addresses";
    public static final String ES_STATUS_INDEX_ADDRESSES = "config.es.status.addresses";

    private final JsonNodeService jsonNodeService;
    private final IndexService indexService;
    private final LoadFiltersService loadFiltersService;
    private final ElasticsearchProperties elasticsearchRestClientProperties;
    private final ApplicationProperties applicationProperties;

    public ElasticConfigurationService(
        final JsonNodeService jsonNodeService,
        final IndexService indexService,
        final LoadFiltersService loadFiltersService,
        final ElasticsearchProperties elasticsearchRestClientProperties,
        final ApplicationProperties applicationProperties
    ) {
        this.jsonNodeService = jsonNodeService;
        this.indexService = indexService;
        this.loadFiltersService = loadFiltersService;
        this.elasticsearchRestClientProperties = elasticsearchRestClientProperties;
        this.applicationProperties = applicationProperties;
    }

    public JsonNode setup(final JsonNode configuration, final Acquisition acquisition) {
        final Long acquisitionId = acquisition.getId();
        final UUID workflowId = acquisition.getWorkflowId();
        createIndicesAndUpdateConfig(configuration, acquisitionId, workflowId);
        indexFilters(acquisition, configuration);
        updateElasticSearchAddress(configuration);
        indexSources(acquisition);
        return configuration;
    }

    private void indexSources(Acquisition acquisition) {
        final Crawler crawler = acquisition.getCrawler();
        final Set<Source> sources = crawler.getSources();

        final UUID workflowId = acquisition.getWorkflowId();
        final Long acquisitionId = acquisition.getId();

        String indexName = IndexService.buildIndexName(applicationProperties.getStatusIndexSuffix(), workflowId, acquisitionId);
        Integer size = applicationProperties.getMaxElkSourcesBulkSize();
        Integer factor = applicationProperties.getElkSourcesBulkSizeFactor();

        if (sources.size() < factor * size) {
            sources.forEach(s -> indexService.createStormSource(indexName, s.getUrl()));
        } else {
            PartitionList.ofSize(new ArrayList<>(sources), size).forEach(batch -> indexService.bulkCreateStormSource(indexName, batch));
        }
    }

    private void createIndicesAndUpdateConfig(JsonNode configuration, Long acquisitionId, UUID workflowId) {
        updateConfigIndexName(configuration, workflowId, acquisitionId);
        updateContentIndexName(configuration, workflowId, acquisitionId);
        updateMetricsIndexName(configuration, workflowId, acquisitionId);
        updateStatusIndexName(configuration, workflowId, acquisitionId);
        indexService.setupIndices(workflowId, acquisitionId);
    }

    private void indexFilters(Acquisition acquisition, JsonNode configuration) {
        final Crawler crawler = acquisition.getCrawler();
        final UUID workflowId = acquisition.getWorkflowId();
        final Long acquisitionId = acquisition.getId();

        JsonNode parseFilters = loadFiltersService.loadParseFilters(crawler).orElseThrow();
        indexService.createParseFilter(workflowId, acquisitionId, parseFilters.toString());

        JsonNode urlFilters = loadFiltersService.loadUrlFilters(crawler).orElseThrow();
        indexService.createUrlFilter(workflowId, acquisitionId, urlFilters.toString());

        JsonNode wihpUrlFilters = loadFiltersService.loadWihpUrlFilters(crawler).orElseThrow();
        indexService.createWihpUrlFilter(workflowId, acquisitionId, wihpUrlFilters.toString());

        JsonNode wihpParseFilters = loadFiltersService.loadWihpParseFilters(crawler).orElseThrow();
        indexService.createWihpParseFilter(workflowId, acquisitionId, wihpParseFilters.toString());

        loadFiltersService.applyUrlFiltersConfigFile(configuration, workflowId, acquisitionId);
        loadFiltersService.applyParseFiltersConfigFile(configuration, workflowId, acquisitionId);
    }

    protected void updateConfigIndexName(JsonNode configuration, UUID workflowId, Long acquisitionId) {
        updateIndexName(configuration, workflowId, acquisitionId, CONFIG);
    }

    protected void updateContentIndexName(JsonNode configuration, UUID workflowId, Long acquisitionId) {
        updateIndexName(configuration, workflowId, acquisitionId, CONTENT);
    }

    protected void updateMetricsIndexName(JsonNode configuration, UUID workflowId, Long acquisitionId) {
        updateIndexName(configuration, workflowId, acquisitionId, METRICS);
    }

    protected void updateStatusIndexName(JsonNode configuration, UUID workflowId, Long acquisitionId) {
        updateIndexName(configuration, workflowId, acquisitionId, STATUS);
    }

    private void updateIndexName(JsonNode configuration, UUID workflowId, Long acquisitionId, ElasticConfigurationEnum configurationEnum) {
        String configIndexName = IndexService.buildIndexName(configurationEnum.prefix, workflowId, acquisitionId);
        jsonNodeService.setProperty(configuration, configurationEnum.propertyPath, configIndexName);
    }

    private void updateElasticSearchAddress(final JsonNode configuration) {
        updateElasticSearchConfigAddresses(configuration);
        updateElasticSearchContentAddresses(configuration);
        updateElasticSearchMetricsAddresses(configuration);
        updateElasticSearchStatusAddresses(configuration);
    }

    protected void updateElasticSearchConfigAddresses(final JsonNode configuration) {
        updateElasticSearchIndex(configuration, ES_CONFIG_INDEX_ADDRESSES);
    }

    protected void updateElasticSearchStatusAddresses(final JsonNode configuration) {
        updateElasticSearchIndex(configuration, ES_STATUS_INDEX_ADDRESSES);
    }

    protected void updateElasticSearchMetricsAddresses(final JsonNode configuration) {
        updateElasticSearchIndex(configuration, ES_METRICS_INDEX_ADDRESSES);
    }

    protected void updateElasticSearchContentAddresses(final JsonNode configuration) {
        updateElasticSearchIndex(configuration, ES_CONTENT_INDEX_ADDRESSES);
    }

    private void updateElasticSearchIndex(JsonNode configuration, String esContentIndexAddresses) {
        final List<String> hosts = elasticsearchRestClientProperties.getUris();
        jsonNodeService.setProperty(configuration, esContentIndexAddresses, hosts);
    }
}
