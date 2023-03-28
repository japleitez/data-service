package eu.europa.ec.eurostat.wihp.service.dto;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.domain.ParserFilter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class ParserFilterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull
    @Size(min = 1)
    @Pattern(regexp = ParserFilter.CLASSNAME_PATTERN)
    private String className;

    @NotNull
    @Size(min = 1, max = 255)
    @Pattern(regexp = ParserFilter.NAME_PATTERN)
    private String name;

    private JsonNode params;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JsonNode getParams() {
        return params;
    }

    public void setParams(JsonNode params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "ParserFilterDTO{" + "id=" + id + ", className='" + className + '\'' + ", name='" + name + '\'' + ", params=" + params + '}';
    }
}
