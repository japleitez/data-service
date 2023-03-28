package eu.europa.ec.eurostat.wihp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class CrawlerResponseDTO {

    @JsonProperty("fieldErrors")
    List<CrawlerFieldResponseDTO> fieldErrors;
    @JsonProperty("sourceErrors")
    List<SourceResponseDTO> sourceErrors;

    public CrawlerResponseDTO() { // needed from jackson
        this.fieldErrors = new ArrayList<>();
        this.sourceErrors = new ArrayList<>();
    }

    public CrawlerResponseDTO(List<CrawlerFieldResponseDTO> fieldErrors) {
        this.fieldErrors = fieldErrors;
        this.sourceErrors = new ArrayList<>();
    }

    public List<CrawlerFieldResponseDTO> getFieldErrors() {
        return fieldErrors;
    }

    public List<SourceResponseDTO> getSourceErrors() {
        return sourceErrors;
    }


    public void setSourceErrors(List<SourceResponseDTO> sourceErrors) {
        this.sourceErrors = sourceErrors;
    }


}
