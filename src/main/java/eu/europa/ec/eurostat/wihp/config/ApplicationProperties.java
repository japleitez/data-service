package eu.europa.ec.eurostat.wihp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import tech.jhipster.config.JHipsterProperties;

import java.util.List;

/**
 * Properties specific to Data Acquisition Service.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private List<String> nimbusSeeds;
    private String topologyPath;
    private String killTopologyWaitTime;
    private String topologyReportAddress;
    private String seleniumAddress;
    private String fluxPath;
    private String fluxExt;
    private String parseFilterIndexId;
    private String urlFilterIndexId;
    private String stormMetricsTemplate;
    private String configFile;
    private String contentFile;
    private String metricsFile;
    private String statusFile;
    private String configIndexSuffix;
    private String contentIndexSuffix;
    private String metricsIndexSuffix;
    private String statusIndexSuffix;
    private Integer maxSourcesBulkSize;
    private Integer maxElkSourcesBulkSize;
    private Integer elkSourcesBulkSizeFactor;
    private List<String> oauth2Groups;
    private List<String> oauth2Scopes;
    private List<String> urlFilterClasses;
    private List<String> parseFilterClasses;
    private String playgroundServiceAddress;
    private String wihpUrlFilterJsonTemplate;
    private String wihpUrlFilterClass;
    private String wihpUrlFilterIndexId;
    private String wihpParseFilterJsonTemplate;
    private String wihpParseFilterClass;
    private String wihpParseFilterIndexId;

    public List<String> getNimbusSeeds() {
        return nimbusSeeds;
    }

    public void setNimbusSeeds(List<String> nimbusSeeds) {
        this.nimbusSeeds = nimbusSeeds;
    }

    public String getTopologyPath() {
        return topologyPath;
    }

    public void setTopologyPath(String topologyPath) {
        this.topologyPath = topologyPath;
    }

    public String getKillTopologyWaitTime() {
        return killTopologyWaitTime;
    }

    public void setKillTopologyWaitTime(String killTopologyWaitTime) {this.killTopologyWaitTime = killTopologyWaitTime;}

    public String getTopologyReportAddress() {
        return topologyReportAddress;
    }

    public void setTopologyReportAddress(String topologyReportAddress) {this.topologyReportAddress = topologyReportAddress;}

    public String getSeleniumAddress() {
        return seleniumAddress;
    }

    public void setSeleniumAddress(String seleniumAddress) {
        this.seleniumAddress = seleniumAddress;
    }

    public String getFluxPath() {return fluxPath;}

    public void setFluxPath(String fluxPath) {this.fluxPath = fluxPath;}

    public String getFluxExt() {return fluxExt;}

    public void setFluxExt(String fluxExt) {this.fluxExt = fluxExt;}

    public List<String> getOauth2Groups() {
        return oauth2Groups;
    }

    public void setOauth2Groups(List<String> oauth2Groups) {
        this.oauth2Groups = oauth2Groups;
    }

    public void setOauth2Scopes(final List<String> oauth2Scopes) {
        this.oauth2Scopes = oauth2Scopes;
    }

    public List<String> getOauth2Scopes() {
        return oauth2Scopes;
    }

    public void setUrlFilterClasses(List<String> urlFilterClasses) {
        this.urlFilterClasses = urlFilterClasses;
    }

    public void setParseFilterClasses(List<String> parseFilterClasses) {
        this.parseFilterClasses = parseFilterClasses;
    }

    public List<String> getUrlFilterClasses() {
        return urlFilterClasses;
    }

    public List<String> getParseFilterClasses() {
        return parseFilterClasses;
    }

    public String getParseFilterIndexId() {return parseFilterIndexId;}

    public void setParseFilterIndexId(String parseFilterIndexId) {this.parseFilterIndexId = parseFilterIndexId;}

    public String getUrlFilterIndexId() {return urlFilterIndexId;}

    public void setUrlFilterIndexId(String urlFilterIndexId) {this.urlFilterIndexId = urlFilterIndexId;}

    public String getStormMetricsTemplate() {return stormMetricsTemplate;}

    public void setStormMetricsTemplate(String stormMetricsTemplate) {this.stormMetricsTemplate = stormMetricsTemplate;}

    public String getConfigFile() {return configFile;}

    public void setConfigFile(String configFile) {this.configFile = configFile;}

    public String getContentFile() {return contentFile;}

    public void setContentFile(String contentFile) {this.contentFile = contentFile;}

    public String getMetricsFile() {return metricsFile;}

    public void setMetricsFile(String metricsFile) {this.metricsFile = metricsFile;}

    public String getStatusFile() {return statusFile;}

    public void setStatusFile(String statusFile) {this.statusFile = statusFile;}

    public String getConfigIndexSuffix() {return configIndexSuffix;}

    public void setConfigIndexSuffix(String configIndexSuffix) {this.configIndexSuffix = configIndexSuffix;}

    public String getContentIndexSuffix() {return contentIndexSuffix;}

    public void setContentIndexSuffix(String contentIndexSuffix) {this.contentIndexSuffix = contentIndexSuffix;}

    public String getMetricsIndexSuffix() {return metricsIndexSuffix;}

    public void setMetricsIndexSuffix(String metricsIndexSuffix) {this.metricsIndexSuffix = metricsIndexSuffix;}

    public String getStatusIndexSuffix() {return statusIndexSuffix;}

    public void setStatusIndexSuffix(String statusIndexSuffix) {this.statusIndexSuffix = statusIndexSuffix;}

    public Integer getMaxSourcesBulkSize() {return maxSourcesBulkSize;}

    public void setMaxSourcesBulkSize(Integer maxSourcesBulkSize) {this.maxSourcesBulkSize = maxSourcesBulkSize;}

    public Integer getMaxElkSourcesBulkSize() {return maxElkSourcesBulkSize;}

    public void setMaxElkSourcesBulkSize(Integer maxElkSourcesBulkSize) {this.maxElkSourcesBulkSize = maxElkSourcesBulkSize;}

    public Integer getElkSourcesBulkSizeFactor() {return elkSourcesBulkSizeFactor;}

    public void setElkSourcesBulkSizeFactor(Integer elkSourcesBulkSizeFactor) {this.elkSourcesBulkSizeFactor = elkSourcesBulkSizeFactor;}

    public String getPlaygroundServiceAddress() { return playgroundServiceAddress; }

    public void setPlaygroundServiceAddress(String playgroundServiceAddress) { this.playgroundServiceAddress = playgroundServiceAddress; }

    public String getWihpUrlFilterJsonTemplate() { return wihpUrlFilterJsonTemplate; }

    public void setWihpUrlFilterJsonTemplate(String wihpUrlFilterJsonTemplate) { this.wihpUrlFilterJsonTemplate = wihpUrlFilterJsonTemplate; }

    public String getWihpUrlFilterClass() { return wihpUrlFilterClass; }

    public void setWihpUrlFilterClass(String wihpUrlFilterClass) { this.wihpUrlFilterClass = wihpUrlFilterClass; }

    public String getWihpUrlFilterIndexId() { return wihpUrlFilterIndexId; }

    public void setWihpUrlFilterIndexId(String wihpUrlFilterIndexId) { this.wihpUrlFilterIndexId = wihpUrlFilterIndexId; }

    public String getWihpParseFilterJsonTemplate() { return wihpParseFilterJsonTemplate; }

    public void setWihpParseFilterJsonTemplate(String wihpParseFilterJsonTemplate) { this.wihpParseFilterJsonTemplate = wihpParseFilterJsonTemplate; }

    public String getWihpParseFilterClass() { return wihpParseFilterClass; }

    public void setWihpParseFilterClass(String wihpParseFilterClass) { this.wihpParseFilterClass = wihpParseFilterClass; }

    public String getWihpParseFilterIndexId() { return wihpParseFilterIndexId; }

    public void setWihpParseFilterIndexId(String wihpParseFilterIndexId) { this.wihpParseFilterIndexId = wihpParseFilterIndexId; }
}
