package eu.europa.ec.eurostat.wihp.storm.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.domain.UrlFilter;

public class WihpUrlFiltersWrapper {

    @JsonProperty(value = "class")
    private String className;
    @JsonProperty(value = "configuration")
    private JsonNode configuration;

    public WihpUrlFiltersWrapper(UrlFilter urlFilter) {
        this.className = urlFilter.getFilterId();
        this.configuration = urlFilter.getConfiguration();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public JsonNode getConfiguration() {
        return configuration;
    }

    public void setConfiguration(JsonNode configuration) {
        this.configuration = configuration;
    }
}
