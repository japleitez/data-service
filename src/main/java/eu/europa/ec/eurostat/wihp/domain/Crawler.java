package eu.europa.ec.eurostat.wihp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.util.ExcludeJacocoGeneratedReport;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Crawler.
 */
@Entity
@Table(name = "crawler")
@ExcludeJacocoGeneratedReport
public class Crawler implements Serializable {

    public static final String NAME_REGEX = "^(?=.*[a-zA-Z\\d].*)[a-zA-Z\\d_-]{1,}$";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(min = 1, max = 100)
    @Pattern(regexp = NAME_REGEX)
    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @NotNull
    @Min(value = -1)
    @Max(value = 525600)
    @Column(name = "fetch_interval", nullable = false)
    private Integer fetchInterval;

    @NotNull
    @Min(value = -1)
    @Max(value = 525600)
    @Column(name = "fetch_interval_when_error", nullable = false)
    private Integer fetchIntervalWhenError;

    @NotNull
    @Min(value = -1)
    @Max(value = 525600)
    @Column(name = "fetch_interval_when_fetch_error", nullable = false)
    private Integer fetchIntervalWhenFetchError;

    @NotNull
    @Column(name = "extractor_no_text", nullable = false)
    private Boolean extractorNoText;

    @Column(name = "extractor_text_include_pattern")
    private String extractorTextIncludePattern;

    @Column(name = "extractor_text_exclude_tags")
    private String extractorTextExcludeTags;

    @NotNull
    @Min(value = -1)
    @Max(value = 2147483647)
    @Column(name = "http_content_limit", nullable = false)
    private Integer httpContentLimit;

    @NotNull
    @Column(name = "emit_out_links", nullable = false)
    private Boolean emitOutLinks;

    @NotNull
    @Min(value = -1)
    @Max(value = 2147483647)
    @Column(name = "max_emit_out_links_per_page", nullable = false)
    private Integer maxEmitOutLinksPerPage;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "crawler_id")
    @JsonIgnoreProperties(value = {"crawler"}, allowSetters = true)
    private Set<ParserFilter> parserFilters = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "crawler_id")
    private Set<UrlFilter> urlFilters = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "crawler_id")
    private Set<ParseFilter> parseFilters = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "crawler_id")
    @JsonIgnoreProperties(value = {"crawler"}, allowSetters = true)
    private Set<Acquisition> acquisitions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_crawler__source",
        joinColumns = @JoinColumn(name = "crawler_id"),
        inverseJoinColumns = @JoinColumn(name = "source_id")
    )
    @JsonIgnoreProperties(value = {"crawlers"}, allowSetters = true)
    private Set<Source> sources = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    @NotNull
    @Column(name = "dynamic", nullable = false)
    private Boolean dynamic;

    @Type(type = "json")
    @Column(name = "dynamic_config", columnDefinition = "jsonb")
    private JsonNode dynamicConfig;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Crawler id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Crawler name(String name) {
        this.name = name;
        return this;
    }

    public Integer getFetchInterval() {
        return this.fetchInterval;
    }

    public void setFetchInterval(Integer fetchInterval) {
        this.fetchInterval = fetchInterval;
    }

    public Crawler fetchInterval(Integer fetchInterval) {
        this.fetchInterval = fetchInterval;
        return this;
    }

    public Integer getFetchIntervalWhenError() {
        return this.fetchIntervalWhenError;
    }

    public void setFetchIntervalWhenError(Integer fetchIntervalWhenError) {
        this.fetchIntervalWhenError = fetchIntervalWhenError;
    }

    public Crawler fetchIntervalWhenError(Integer fetchIntervalWhenError) {
        this.fetchIntervalWhenError = fetchIntervalWhenError;
        return this;
    }

    public Integer getFetchIntervalWhenFetchError() {
        return this.fetchIntervalWhenFetchError;
    }

    public void setFetchIntervalWhenFetchError(Integer fetchIntervalWhenFetchError) {
        this.fetchIntervalWhenFetchError = fetchIntervalWhenFetchError;
    }

    public Crawler fetchIntervalWhenFetchError(Integer fetchIntervalWhenFetchError) {
        this.fetchIntervalWhenFetchError = fetchIntervalWhenFetchError;
        return this;
    }

    public Boolean getExtractorNoText() {
        return this.extractorNoText;
    }

    public void setExtractorNoText(Boolean extractorNoText) {
        this.extractorNoText = extractorNoText;
    }

    public Crawler extractorNoText(Boolean extractorNoText) {
        this.extractorNoText = extractorNoText;
        return this;
    }

    public String getExtractorTextIncludePattern() {
        return this.extractorTextIncludePattern;
    }

    public void setExtractorTextIncludePattern(String extractorTextIncludePattern) {
        this.extractorTextIncludePattern = extractorTextIncludePattern;
    }

    public Crawler extractorTextIncludePattern(String extractorTextIncludePattern) {
        this.extractorTextIncludePattern = extractorTextIncludePattern;
        return this;
    }

    public String getExtractorTextExcludeTags() {
        return this.extractorTextExcludeTags;
    }

    public void setExtractorTextExcludeTags(String extractorTextExcludeTags) {
        this.extractorTextExcludeTags = extractorTextExcludeTags;
    }

    public Crawler extractorTextExcludeTags(String extractorTextExcludeTags) {
        this.extractorTextExcludeTags = extractorTextExcludeTags;
        return this;
    }

    public Integer getHttpContentLimit() {
        return this.httpContentLimit;
    }

    public void setHttpContentLimit(Integer httpContentLimit) {
        this.httpContentLimit = httpContentLimit;
    }

    public Crawler httpContentLimit(Integer httpContentLimit) {
        this.httpContentLimit = httpContentLimit;
        return this;
    }

    public Boolean getEmitOutLinks() {
        return this.emitOutLinks;
    }

    public void setEmitOutLinks(Boolean emitOutLinks) {
        this.emitOutLinks = emitOutLinks;
    }

    public Crawler emitOutLinks(Boolean emitOutLinks) {
        this.emitOutLinks = emitOutLinks;
        return this;
    }

    public Integer getMaxEmitOutLinksPerPage() {
        return this.maxEmitOutLinksPerPage;
    }

    public void setMaxEmitOutLinksPerPage(Integer maxEmitOutLinksPerPage) {
        this.maxEmitOutLinksPerPage = maxEmitOutLinksPerPage;
    }

    public Crawler maxEmitOutLinksPerPage(Integer maxEmitOutLinksPerPage) {
        this.maxEmitOutLinksPerPage = maxEmitOutLinksPerPage;
        return this;
    }

    public Set<ParserFilter> getParserFilters() {
        return this.parserFilters;
    }

    public void setParserFilters(Set<ParserFilter> parserFilters) {
        if (this.parserFilters != null) {
            this.parserFilters.forEach(i -> i.setCrawler(null));
        }
        if (parserFilters != null) {
            parserFilters.forEach(i -> i.setCrawler(this));
        }
        this.parserFilters = parserFilters;
    }

    public Crawler parserFilters(Set<ParserFilter> parserFilters) {
        this.setParserFilters(parserFilters);
        return this;
    }

    public Crawler addParserFilter(ParserFilter parserFilter) {
        this.parserFilters.add(parserFilter);
        parserFilter.setCrawler(this);
        return this;
    }

    public Set<UrlFilter> getUrlFilters() {
        return this.urlFilters;
    }

    public void setUrlFilters(Set<UrlFilter> urlFilters) {
        if (this.urlFilters != null) {
            this.urlFilters.forEach(i -> i.setCrawler(null));
        }
        if (urlFilters != null) {
            urlFilters.forEach(i -> i.setCrawler(this));
        }
        this.urlFilters = urlFilters;
    }

    public Crawler urlFilters(Set<UrlFilter> urlFilters) {
        this.setUrlFilters(urlFilters);
        return this;
    }

    public Crawler addUrlFilter(UrlFilter urlFilter) {
        this.urlFilters.add(urlFilter);
        urlFilter.setCrawler(this);
        return this;
    }

    public Set<ParseFilter> getParseFilters() {
        return this.parseFilters;
    }

    public void setParseFilters(Set<ParseFilter> parseFilters) {
        if (this.parseFilters != null) {
            this.parseFilters.forEach(item -> item.setCrawler(null));
        }
        if (parseFilters != null) {
            parseFilters.forEach(item -> item.setCrawler(this));
        }
        this.parseFilters = parseFilters;
    }

    public Crawler parseFilters(Set<ParseFilter> parseFilters) {
        this.setParseFilters(parseFilters);
        return this;
    }

    public Crawler addParseFilter(ParseFilter parseFilter) {
        this.parseFilters.add(parseFilter);
        parseFilter.setCrawler(this);
        return this;
    }

    public Set<Acquisition> getAcquisitions() {
        return this.acquisitions;
    }

    public void setAcquisitions(Set<Acquisition> acquisitions) {
        if (this.acquisitions != null) {
            this.acquisitions.forEach(i -> i.setCrawler(null));
        }
        if (acquisitions != null) {
            acquisitions.forEach(i -> i.setCrawler(this));
        }
        this.acquisitions = acquisitions;
    }

    public Crawler acquisitions(Set<Acquisition> acquisitions) {
        this.setAcquisitions(acquisitions);
        return this;
    }

    public Set<Source> getSources() {
        return this.sources;
    }

    public void setSources(Set<Source> sources) {
        this.sources = sources;
    }

    public Crawler sources(Set<Source> sources) {
        this.setSources(sources);
        return this;
    }

    public Crawler addSource(Source source) {
        this.sources.add(source);
        source.getCrawlers().add(this);
        return this;
    }

    public Crawler removeSource(Source source) {
        this.sources.remove(source);
        source.getCrawlers().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    public Boolean getDynamic() {
        return dynamic;
    }

    public void setDynamic(Boolean dynamic) {
        this.dynamic = dynamic;
    }

    public Crawler dynamic(Boolean dynamic) {
        this.dynamic = dynamic;
        return this;
    }

    public JsonNode getDynamicConfig() {
        return dynamicConfig;
    }

    public void setDynamicConfig(JsonNode dynamicConfig) {
        this.dynamicConfig = dynamicConfig;
    }

    public Crawler dynamicConfig(JsonNode dynamicConfig) {
        this.dynamicConfig = dynamicConfig;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Crawler)) {
            return false;
        }
        return id != null && id.equals(((Crawler) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Crawler{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", fetchInterval=" + getFetchInterval() +
            ", fetchIntervalWhenError=" + getFetchIntervalWhenError() +
            ", fetchIntervalWhenFetchError=" + getFetchIntervalWhenFetchError() +
            ", extractorNoText='" + getExtractorNoText() + "'" +
            ", extractorTextIncludePattern='" + getExtractorTextIncludePattern() + "'" +
            ", extractorTextExcludeTags='" + getExtractorTextExcludeTags() + "'" +
            ", httpContentLimit=" + getHttpContentLimit() +
            ", emitOutLinks='" + getEmitOutLinks() + "'" +
            ", maxEmitOutLinksPerPage=" + getMaxEmitOutLinksPerPage() +
            ", dynamic=" + getDynamic() +
            ", dynamicConfig=" + getDynamicConfig() +
            "}";
    }
}
