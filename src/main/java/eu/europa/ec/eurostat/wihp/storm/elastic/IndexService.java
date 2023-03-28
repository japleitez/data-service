package eu.europa.ec.eurostat.wihp.storm.elastic;

import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.domain.Source;
import eu.europa.ec.eurostat.wihp.util.JsonNodeUtils;
import eu.europa.ec.eurostat.wihp.util.ResourcesUtils;
import org.apache.commons.io.FileUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

@Service
public class IndexService {

    private static final Logger log = LoggerFactory.getLogger(IndexService.class);
    final ElasticService service;
    final ResourcesUtils resources = new ResourcesUtils();
    private final ApplicationProperties applicationProperties;

    public IndexService(final ElasticService service, ApplicationProperties applicationProperties) {
        this.service = service;
        this.applicationProperties = applicationProperties;
    }

    public static String buildIndexName(String prefix, UUID workflowId, Long acquisitionId) {
        return String.format("%s_%s_%d", prefix, workflowId.toString(), acquisitionId);
    }

    public void setupIndices(UUID workflowId, Long acquisitionId) {
        createStatusIndex(workflowId, acquisitionId);
        createContentIndex(workflowId, acquisitionId);
        createConfigIndex(workflowId, acquisitionId);
        createMetricsTemplate();
    }

    protected void createStatusIndex(UUID workflowId, Long acquisitionId) {
        createIndex(applicationProperties.getStatusIndexSuffix(), workflowId, acquisitionId, resources.getFile(applicationProperties.getStatusFile()));
    }

    protected void createContentIndex(UUID workflowId, Long acquisitionId) {
        createIndex(applicationProperties.getContentIndexSuffix(), workflowId, acquisitionId, resources.getFile(applicationProperties.getContentFile()));
    }

    protected void createConfigIndex(UUID workflowId, Long acquisitionId) {
        createIndex(applicationProperties.getConfigIndexSuffix(), workflowId, acquisitionId, resources.getFile(applicationProperties.getConfigFile()));
    }

    public void createParseFilter(UUID workflowId, Long acquisitionId, String json) {
        String indexName = IndexService.buildIndexName(applicationProperties.getConfigIndexSuffix(), workflowId, acquisitionId);
        try {
            service.createIndexId(indexName, applicationProperties.getParseFilterIndexId(), json);
        } catch (Exception e) {
            logCreateIndexException("Error to create parseFilter for Index ", indexName, e);
        }
    }

    public String createUrlFilter(UUID workflowId, Long acquisitionId, String json) {
        String indexName = IndexService.buildIndexName(applicationProperties.getConfigIndexSuffix(), workflowId, acquisitionId);
        try {
            service.createIndexId(indexName, applicationProperties.getUrlFilterIndexId(), json);
            return applicationProperties.getUrlFilterIndexId();
        } catch (Exception e) {
            logCreateIndexException("Error to create urlFilter for Index ", indexName, e);
        }
        return "";
    }

    public String createWihpUrlFilter(UUID workflowId, Long acquisitionId, String json) {
        String indexName = IndexService.buildIndexName(applicationProperties.getConfigIndexSuffix(), workflowId, acquisitionId);
        try {
            service.createIndexId(indexName, applicationProperties.getWihpUrlFilterIndexId(), json);
            return applicationProperties.getWihpUrlFilterIndexId();
        } catch (Exception e) {
            logCreateIndexException("Error to create WihpUrlFilter for Index", indexName, e);
        }
        return "";
    }

    public String createWihpParseFilter(UUID workflowId, Long acquisitionId, String json) {
        String indexName = IndexService.buildIndexName(applicationProperties.getConfigIndexSuffix(), workflowId, acquisitionId);
        try {
            service.createIndexId(indexName, applicationProperties.getWihpParseFilterIndexId(), json);
            return applicationProperties.getWihpParseFilterIndexId();
        } catch (Exception e) {
            logCreateIndexException("Error to create WihpParseFilter for Index", indexName, e);
        }
        return "";
    }

    public String createStormSource(String indexName, String link) {
        String id = sha256Hex(link);
        StormSource stormSource = new StormSource(link);
        String jsonString = JsonNodeUtils.createJsonString(stormSource);
        service.createIndexId(indexName, id, jsonString, stormSource.getKey());
        return id;
    }

    public void bulkCreateStormSource(String indexName, List<Source> sList) {
        BulkRequest bulkRequest = new BulkRequest();
        sList.forEach(s -> bulkRequest.add(createIndexRequest(indexName, s)));
        service.createBulkIndexId(bulkRequest);
    }

    private IndexRequest createIndexRequest(String indexName, Source s) {
        StormSource stormSource = new StormSource(s.getUrl());
        String jsonString = JsonNodeUtils.createJsonString(stormSource);
        IndexRequest request = new IndexRequest(indexName);
        request.id(sha256Hex(s.getUrl()));
        request.source(jsonString, XContentType.JSON);
        if (StringUtils.hasText(stormSource.getKey())) {
            request.routing(stormSource.getKey());
        }
        return request;
    }

    protected void createMetricsTemplate() {
        createTemplate(resources.getFile(applicationProperties.getMetricsFile()));
    }

    protected String createIndex(final String prefix, UUID workflowId, Long acquisitionId, File json) {
        String indexName = IndexService.buildIndexName(prefix, workflowId, acquisitionId);
        try {
            service.createIndex(indexName, FileUtils.readFileToString(json, StandardCharsets.UTF_8));
            return indexName;
        } catch (Exception e) {
            logCreateIndexException("Error to create index Metrics for Index ", indexName, e);
        }
        return "";
    }

    private void createTemplate(File json) {
        try {
            service.createTemplate(applicationProperties.getStormMetricsTemplate(), FileUtils.readFileToString(json, StandardCharsets.UTF_8));
        } catch (IOException e) {
            logCreateIndexException("Error to create template ", applicationProperties.getStormMetricsTemplate(), e);
        }
    }

    private void logCreateIndexException(String message, String indexName, Exception expt) {
        log.error("{} {} Exception {} ", message, indexName, expt);
        throw new ElasticsearchException(message + " for index={}, Exception={}", indexName, expt);
    }
}
