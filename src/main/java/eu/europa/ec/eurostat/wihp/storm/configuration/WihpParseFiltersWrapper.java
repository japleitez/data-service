package eu.europa.ec.eurostat.wihp.storm.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.domain.ParseFilter;

public class WihpParseFiltersWrapper {

    @JsonProperty(value = "class")
    private String className;
    @JsonProperty(value = "configuration")
    private JsonNode configuration;

    public WihpParseFiltersWrapper(ParseFilter parseFilter) {
        this.className = parseFilter.getFilterId();
        this.configuration = parseFilter.getConfiguration();
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
