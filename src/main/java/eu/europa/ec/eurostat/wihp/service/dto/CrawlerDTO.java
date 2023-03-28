package eu.europa.ec.eurostat.wihp.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.service.validation.ParserFilterParams;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.*;

public class CrawlerDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 1, max = 100)
    @Pattern(regexp = Crawler.NAME_REGEX)
    private String name;

    @NotNull
    @Min(value = -1)
    @Max(value = 525600)
    private Integer fetchInterval;

    @NotNull
    @Min(value = -1)
    @Max(value = 525600)
    private Integer fetchIntervalWhenError;

    @NotNull
    @Min(value = -1)
    @Max(value = 525600)
    private Integer fetchIntervalWhenFetchError;

    @NotNull
    private Boolean extractorNoText;

    private String extractorTextIncludePattern;

    private String extractorTextExcludeTags;

    @NotNull
    @Min(value = -1)
    @Max(value = 2147483647)
    private Integer httpContentLimit;

    @NotNull
    private Boolean emitOutLinks;

    @NotNull
    @Min(value = -1)
    @Max(value = 2147483647)
    private Integer maxEmitOutLinksPerPage;

    @ParserFilterParams
    private Set<ParserFilterDTO> parserFilters = new HashSet<>();

    private Set<UrlFilterDTO> urlFilters = new HashSet<>();

    private Set<ParseFilterDTO> parseFilters = new HashSet<>();

    @JsonIgnoreProperties(value = { "crawlers" }, allowSetters = true)
    private Set<SourceDTO> sources = new HashSet<>();

    @NotNull
    private Boolean dynamic;

    @Valid
    private DynamicConfigDTO dynamicConfig;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFetchInterval() {
        return fetchInterval;
    }

    public void setFetchInterval(Integer fetchInterval) {
        this.fetchInterval = fetchInterval;
    }

    public Integer getFetchIntervalWhenError() {
        return fetchIntervalWhenError;
    }

    public void setFetchIntervalWhenError(Integer fetchIntervalWhenError) {
        this.fetchIntervalWhenError = fetchIntervalWhenError;
    }

    public Integer getFetchIntervalWhenFetchError() {
        return fetchIntervalWhenFetchError;
    }

    public void setFetchIntervalWhenFetchError(Integer fetchIntervalWhenFetchError) {
        this.fetchIntervalWhenFetchError = fetchIntervalWhenFetchError;
    }

    public Boolean getExtractorNoText() {
        return extractorNoText;
    }

    public void setExtractorNoText(Boolean extractorNoText) {
        this.extractorNoText = extractorNoText;
    }

    public String getExtractorTextIncludePattern() {
        return extractorTextIncludePattern;
    }

    public void setExtractorTextIncludePattern(String extractorTextIncludePattern) {
        this.extractorTextIncludePattern = extractorTextIncludePattern;
    }

    public String getExtractorTextExcludeTags() {
        return extractorTextExcludeTags;
    }

    public void setExtractorTextExcludeTags(String extractorTextExcludeTags) {
        this.extractorTextExcludeTags = extractorTextExcludeTags;
    }

    public Integer getHttpContentLimit() {
        return httpContentLimit;
    }

    public void setHttpContentLimit(Integer httpContentLimit) {
        this.httpContentLimit = httpContentLimit;
    }

    public Boolean getEmitOutLinks() {
        return emitOutLinks;
    }

    public void setEmitOutLinks(Boolean emitOutLinks) {
        this.emitOutLinks = emitOutLinks;
    }

    public Integer getMaxEmitOutLinksPerPage() {
        return maxEmitOutLinksPerPage;
    }

    public void setMaxEmitOutLinksPerPage(Integer maxEmitOutLinksPerPage) {
        this.maxEmitOutLinksPerPage = maxEmitOutLinksPerPage;
    }

    public Set<ParserFilterDTO> getParserFilters() {
        return parserFilters;
    }

    public void setParserFilters(Set<ParserFilterDTO> parserFilters) {
        this.parserFilters = parserFilters;
    }

    public Set<UrlFilterDTO> getUrlFilters() {
        return urlFilters;
    }

    public Set<ParseFilterDTO> getParseFilters() {
        return parseFilters;
    }

    public void setParseFilters(Set<ParseFilterDTO> parseFilters) {
        this.parseFilters = parseFilters;
    }

    public void setUrlFilters(Set<UrlFilterDTO> urlFilters) {
        this.urlFilters = urlFilters;
    }

    public Set<SourceDTO> getSources() {
        return sources;
    }

    public void setSources(Set<SourceDTO> sources) {
        this.sources = sources;
    }

    public Boolean getDynamic() {
        return dynamic;
    }

    public void setDynamic(Boolean dynamic) {
        this.dynamic = dynamic;
    }

    public DynamicConfigDTO getDynamicConfig() {
        return dynamicConfig;
    }

    public void setDynamicConfig(DynamicConfigDTO dynamicConfig) {
        this.dynamicConfig = dynamicConfig;
    }

    public void addSources(List<SourceDTO> sources) {
        this.getSources().addAll(sources);
    }

    public void addSource(SourceDTO source) {
        this.getSources().add(source);
    }

    @Override
    public String toString() {
        return (
            "CrawlerDTO{" +
            "id=" +
            id +
            ", name='" +
            name +
            '\'' +
            ", fetchInterval=" +
            fetchInterval +
            ", fetchIntervalWhenError=" +
            fetchIntervalWhenError +
            ", fetchIntervalWhenFetchError=" +
            fetchIntervalWhenFetchError +
            ", extractorNoText=" +
            extractorNoText +
            ", extractorTextIncludePattern='" +
            extractorTextIncludePattern +
            '\'' +
            ", extractorTextExcludeTags='" +
            extractorTextExcludeTags +
            '\'' +
            ", httpContentLimit=" +
            httpContentLimit +
            ", emitOutLinks=" +
            emitOutLinks +
            ", maxEmitOutLinksPerPage=" +
            maxEmitOutLinksPerPage +
            ", dynamic=" +
            dynamic +
            ", dynamicConfig=" +
            dynamicConfig +
            '}'
        );
    }
}
