package eu.europa.ec.eurostat.wihp.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.europa.ec.eurostat.wihp.domain.Source;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;


@JsonIgnoreProperties(ignoreUnknown = true)
public class SourceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @Size(min = 1, max = 100, message = "name has to be from 1 to 100 characters")
    @Pattern(regexp = Source.NAME_REGEX, message = "The name has illegal character")
    @NotNull(message = "The name cannot be null")
    @JsonProperty("name")
    private String name;

    @URL(message = "The url is not valid")
    @NotNull(message = "The url cannot be null")
    @Size(min = 1)
    @JsonProperty("url")
    private String url;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SourceDTO id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public SourceDTO setName(String name) {
        this.name = name;
        return this;
    }

    public SourceDTO name(String name) {
        return setName(name);
    }

    public String getUrl() {
        return url;
    }

    public SourceDTO setUrl(String url) {
        this.url = url;
        return this;
    }

    public SourceDTO url(String url) {
        return setUrl(url);
    }

    @Override
    public String toString() {
        return "SourceDTO{" + "id=" + id + ", name='" + name + '\'' + ", url='" + url + '\'' + '}';
    }

}
