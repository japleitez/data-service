package eu.europa.ec.eurostat.wihp.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticConfiguration {

    private ElasticsearchProperties properties;

    ElasticConfiguration(ElasticsearchProperties properties) {
        this.properties = properties;
    }

    @Bean(destroyMethod = "close")
    public RestHighLevelClient getRestHighLevelClient() {
        HttpHost[] hosts = properties.getUris().stream().map(HttpHost::create).toArray(HttpHost[]::new);
        return new RestHighLevelClient(RestClient.builder(hosts));
    }
}
