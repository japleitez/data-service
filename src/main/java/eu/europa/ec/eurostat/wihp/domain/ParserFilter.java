package eu.europa.ec.eurostat.wihp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A ParserFilter.
 */
@Entity
@Table(name = "parser_filter")
@TypeDef(name = "json", typeClass = JsonType.class, defaultForType = JsonNode.class) @TypeDef(name = "json", typeClass = JsonType.class, defaultForType = JsonNode.class)
public class ParserFilter implements Serializable {

    public static final String CLASSNAME_PATTERN = "([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*";
    public static final String NAME_PATTERN = "[a-zA-Z_$][a-zA-Z\\d_$]*";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(min = 1)
    @Pattern(regexp = "([\\w.]+)")
    @Column(name = "class_name", nullable = false)
    private String className;

    @NotNull
    @Size(min = 1, max = 255)
    @Pattern(regexp = "[a-zA-Z_$][a-zA-Z\\d_$]*")
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Type(type = "json")
    @Column(name = "params", columnDefinition = "jsonb")
    private JsonNode params;

    @ManyToOne(optional = false)
    @JoinColumn(name = "crawler_id")
    @JsonIgnoreProperties(value = {"parserFilters", "sources", "urlFilters", "parseFilters"}, allowSetters = true)
    private Crawler crawler;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() { return this.name; }

    public void setName(String name) { this.name = name;}

    public JsonNode getParams() {
        return this.params;
    }

    public void setParams(JsonNode params) {
        this.params = params;
    }

    public Crawler getCrawler() {
        return this.crawler;
    }

    public void setCrawler(Crawler crawler) {
        this.crawler = crawler;
    }

    public ParserFilter id(Long id) {
        setId(id);
        return this;
    }

    public ParserFilter className(String className) {
        setClassName(className);
        return this;
    }

    public ParserFilter name(String name) {
        setName(name);
        return this;
    }

    public ParserFilter params(JsonNode params) {
        setParams(params);
        return this;
    }

    public ParserFilter crawler(Crawler crawler) {
        setCrawler(crawler);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParserFilter)) {
            return false;
        }
        return id != null && id.equals(((ParserFilter) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParserFilter{" +
            "id=" + getId() +
            ", className='" + getClassName() + "'" +
            ", name='" + getName() + "'" +
            ", params='" + getParams() + "'" +
            "}";
    }
}
