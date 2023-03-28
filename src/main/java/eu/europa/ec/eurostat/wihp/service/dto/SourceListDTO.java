package eu.europa.ec.eurostat.wihp.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SourceListDTO {

    @JsonProperty("sources")
    @NotEmpty(message = "'sources' cannot be empty")
    private List<SourceDTO> sources;

    public SourceListDTO() {
        this.sources = new ArrayList<>();
    }

    public List<SourceDTO> getSources() {
        return sources;
    }

    public void setSources(List<SourceDTO> sources) {
        this.sources = sources;
    }
}
