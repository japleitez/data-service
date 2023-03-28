package eu.europa.ec.eurostat.wihp.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class  SourceResponseDTO {

    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("url")
    private String url;

    @JsonProperty("errors")
    private List<String> errors;

    public SourceResponseDTO() {
        this.errors = new ArrayList<>();
    }

    public SourceResponseDTO(SourceDTO sourceModel, List<String> errors) {
        this.name = sourceModel.getName();
        this.url = sourceModel.getUrl();
        this.errors = errors;
    }

    public Long getId() {
        return id;
    }

    public SourceResponseDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public SourceResponseDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public SourceResponseDTO setUrl(String url) {
        this.url = url;
        return this;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }


    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SourceResponseDTO other = (SourceResponseDTO) obj;
        return Objects.equals(name, other.name);
    }

}
