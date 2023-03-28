package eu.europa.ec.eurostat.wihp.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CrawlerFieldResponseDTO {

    @JsonProperty("objectName")
    public String objectName;
    @JsonProperty("field")
    public String field;
    @JsonProperty("message")
    public String message;


    public CrawlerFieldResponseDTO setObjectName(String objectName) {
        this.objectName = objectName;
        return this;
    }

    public String getFiled() {
        return field;
    }

    public CrawlerFieldResponseDTO setFiled(String field) {
        this.field = field;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public CrawlerFieldResponseDTO setMessage(String message) {
        this.message = message;
        return this;
    }

}
