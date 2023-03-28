package eu.europa.ec.eurostat.wihp.storm.elastic;

public enum ElasticConfigurationEnum {
    CONFIG("config.es.config.index.name", "config"),
    CONTENT("config.es.indexer.index.name", "content"),
    METRICS("config.es.metrics.index.name", "metrics"),
    STATUS("config.es.status.index.name", "status");

    public final String propertyPath;
    public final String prefix;

    ElasticConfigurationEnum(String propertyPath, String prefix) {
        this.propertyPath = propertyPath;
        this.prefix = prefix;
    }
}
