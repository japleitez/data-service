package eu.europa.ec.eurostat.wihp.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SourceResponseListDTO {

    @JsonProperty("sources")
    public List<SourceResponseDTO> sources;


    public SourceResponseListDTO() {
        this.sources =  new ArrayList<>();
    }

    public List<SourceResponseDTO> getSources() {
        return sources;
    }

    public SourceResponseListDTO setSources(List<SourceResponseDTO> sources) {
        this.sources = sources;
        return this;
    }

}
