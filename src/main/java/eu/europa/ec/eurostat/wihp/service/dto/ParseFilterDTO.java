package eu.europa.ec.eurostat.wihp.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.europa.ec.eurostat.wihp.domain.UrlFilter;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * UI required field 'id' as a name of filter, abd id from DB should not be included to the result json
 */
public class ParseFilterDTO implements CustomFilter, Serializable {

    @JsonIgnore
    private Long id;

    @Size(min = 1)
    @Pattern(regexp = UrlFilter.CLASSNAME_PATTERN)
    @JsonProperty(value = "id")
    private String filterId;

    private ObjectNode configuration;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilterId() {
        return filterId;
    }

    public void setFilterId(String filterId) {
        this.filterId = filterId;
    }

    public JsonNode getConfiguration() {
        return configuration;
    }

    public void setConfiguration(JsonNode configuration) {
        this.configuration = (ObjectNode) configuration;
    }
}
