package eu.europa.ec.eurostat.wihp.storm.elastic;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.GetSourceRequest;
import org.elasticsearch.client.core.GetSourceResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.IndexTemplatesExistRequest;
import org.elasticsearch.client.indices.PutIndexTemplateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Service
public class ElasticService {

    private final RestHighLevelClient client;

    private final Logger log = LoggerFactory.getLogger(ElasticService.class);

    public ElasticService(final RestHighLevelClient client) {
        this.client = client;
    }

    public boolean isConnected() {
        return this.client != null;
    }

    public RestHighLevelClient getClient() {
        return client;
    }

    public boolean createIndexId(String name, String indexId, String jsonSource) {
        return createIndexId(name, indexId, jsonSource, null);
    }

    public boolean createIndexId(String indexName, String indexId, String jsonSource, String routing) {
        try {
            IndexRequest request = new IndexRequest(indexName);
            request.id(indexId);
            request.source(jsonSource, XContentType.JSON);
            if (StringUtils.hasText(routing)) {
                request.routing(routing);
            }
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            if (response.status() == RestStatus.CREATED) {
                return response.status() == RestStatus.CREATED;
            } else {
                String message = String.format("Error for createIndexId name=%s, id=%s, response=%s", indexName, indexId, response.status().toString());
                log.error(message);
                throw new ElasticsearchException(message);
            }
        } catch (IOException e) {
            logAndError(e, "Error for createIndexId with name=" + indexName + ", id=" + indexId + ", source =" + jsonSource + ", routing=" + routing);
        }
        return false;
    }

    public void createBulkIndexId(BulkRequest bulkRequest) {
        BulkResponse response;
        try {
            response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("Error in method createBulkIndexId");
            throw new ElasticsearchException("Error for createIndexId", e);
        }

        if (response.status() == RestStatus.CREATED || response.status() == RestStatus.OK) {
            return;
        }
        String message = String.format("Error in method createBulkIndexId(), RestStatus=%s", response.status().toString());
        log.error(message);
        throw new ElasticsearchException(message);
    }


    public boolean createTemplate(String indexName, String jsonSource) {
        try {
            PutIndexTemplateRequest request = new PutIndexTemplateRequest(indexName);
            request.source(jsonSource, XContentType.JSON);
            return client.indices().putTemplate(request, RequestOptions.DEFAULT).isAcknowledged();
        } catch (IOException e) {
            logAndError(e, "Error for createTemplate index=" + indexName + ", source=" + jsonSource);
        }
        return false;
    }

    public boolean createIndex(String indexName, String jsonSource) {
        try {
            CreateIndexRequest request = new CreateIndexRequest(indexName);
            request.source(jsonSource, XContentType.JSON);
            return client.indices().create(request, RequestOptions.DEFAULT).isAcknowledged();
        } catch (IOException e) {
            logAndError(e, "Error for createIndex index=" + indexName + ", source=" + jsonSource);
        }
        return false;
    }

    public boolean indexExists(String indexName) {
        try {
            GetIndexRequest request = new GetIndexRequest(indexName);
            return client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            logAndError(e, "Error for indexExists name=" + indexName);
        }
        return false;
    }

    public boolean templateExists(String name) {
        try {
            IndexTemplatesExistRequest request = new IndexTemplatesExistRequest(name);
            return client.indices().existsTemplate(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            logAndError(e, "Error for templateExists name=" + name);
        }
        return false;
    }

    public boolean deleteIndexIfExists(String indexName) {
        if (!indexExists(indexName)) {
            return true;
        }
        try {
            DeleteIndexRequest request = new DeleteIndexRequest(indexName);
            return client.indices().delete(request, RequestOptions.DEFAULT).isAcknowledged();
        } catch (IOException e) {
            logAndError(e, "Error for deleteIndexIfExists name=" + indexName);
        }
        return false;
    }

    public String getSourceForIndexAndId(String indexName, String id) {
        try {
            GetSourceRequest request = new GetSourceRequest(indexName, id);
            GetSourceResponse source = client.getSource(request, RequestOptions.DEFAULT);
            return source.toString();
        } catch (IOException e) {
            logAndError(e, "Error for getSourceForIndexAndId index=" + indexName + ", id=" + id);
        }
        return "";
    }

    private void logAndError(IOException e, String message) {
        log.error(message, e);
        throw new ElasticsearchException(message);
    }
}
