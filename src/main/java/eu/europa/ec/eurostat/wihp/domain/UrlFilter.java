package eu.europa.ec.eurostat.wihp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vladmihalcea.hibernate.type.json.JsonType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "url_filter")
@TypeDef(name = "json", typeClass = JsonType.class, defaultForType = JsonNode.class)
public class UrlFilter implements Serializable {

    public static final String CLASSNAME_PATTERN = "([\\w.]+)";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(min = 1)
    @Pattern(regexp = CLASSNAME_PATTERN)
    @Column(name = "filter_id", nullable = false)
    private String filterId;

    @Type(type = "json")
    @Column(name = "configuration", columnDefinition = "jsonb")
    private ObjectNode configuration;

    @ManyToOne(optional = false)
    @JoinColumn(name = "crawler_id")
    @JsonIgnoreProperties(value = {"parserFilters", "sources", "urlFilters"}, allowSetters = true)
    private Crawler crawler;

    public UrlFilter() {
    }

    public UrlFilter(Long id, String filterId, JsonNode configuration, Crawler crawler) {
        this.id = id;
        this.filterId = filterId;
        this.configuration = (ObjectNode)configuration;
        this.crawler = crawler;
    }

    public static UrlFilterBuilder builder() {
        return new UrlFilterBuilder();
    }

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
        this.configuration = (ObjectNode)configuration;
    }

    public Crawler getCrawler() {
        return crawler;
    }

    public void setCrawler(Crawler crawler) {
        this.crawler = crawler;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UrlFilter)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        return filterId != null && filterId.equals(((UrlFilter) obj).filterId);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "UrlFilter(id=" + this.getId()
            + ", filterId=" + this.getFilterId()
            + ", configuration="
            + this.getConfiguration()
            + ", crawler=" + this.getCrawler() + ")";
    }

    public static class UrlFilterBuilder {
        private Long id;
        private @NotNull @Size(min = 1) @Pattern(regexp = CLASSNAME_PATTERN) String filterId;
        private JsonNode configuration;
        private Crawler crawler;

        public UrlFilterBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UrlFilterBuilder filterId(@NotNull @Size(min = 1) @Pattern(regexp = CLASSNAME_PATTERN) String filterId) {
            this.filterId = filterId;
            return this;
        }

        public UrlFilterBuilder configuration(JsonNode configuration) {
            this.configuration = configuration;
            return this;
        }

        public UrlFilterBuilder crawler(Crawler crawler) {
            this.crawler = crawler;
            return this;
        }

        public UrlFilter build() {
            return new UrlFilter(id, filterId, configuration, crawler);
        }
    }
}
