package eu.europa.ec.eurostat.wihp.config.properties;

import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
public class ApplicationPropertiesIT {

    @Autowired
    private ApplicationProperties unit;
    private final int MAX_BULK_SIZE = 999;
    public final static int MAX_ELK_SOURCES_BULK_SIZE = 20;
    public final static int ELK_SOURCES_BULK_SIZE_FACTOR = 10;

    @Test
    public void shouldLoadNimbusSeeds() {
        List<String> result = unit.getNimbusSeeds();

        assertNotNull(result);
        assertTrue(result.contains("nimbus.seed1"));
        assertTrue(result.contains("nimbus.seed2"));
    }

    @Test
    public void shouldLoadTopologyPath() {
        String result = unit.getTopologyPath();

        assertNotNull(result);
        assertTrue(result.contains("/basic-topology.jar"));
    }

    @Test
    public void shouldLoadKillTopologyWaitTime() {
        String result = unit.getKillTopologyWaitTime();

        assertNotNull(result);
        assertEquals("60", result);
    }

    @Test
    public void shouldLoadSeleniumAddress() {
        String result = unit.getSeleniumAddress();

        assertNotNull(result);
        assertEquals("http://host.docker.internal:4444", result);
    }

    @Test
    public void shouldLoadOauth2Groups() {
        List<String> groups = unit.getOauth2Groups();

        assertEquals(2, groups.size());
        assertTrue(groups.contains("WIHP"));
        assertTrue(groups.contains("GRP2"));
    }

    @Test
    public void shouldLoadOauth2Scopes() {
        List<String> scopes = unit.getOauth2Scopes();

        assertEquals(2, scopes.size());
        assertTrue(scopes.contains("scope1"));
        assertTrue(scopes.contains("scope2"));
    }

    @Test
    public void shouldLoadFluxPath() {
        String path = unit.getFluxPath();
        assertTrue(path.contains(".\\acquisition_"));
    }

    @Test
    public void shouldLoadFluxExt() {
        String path = unit.getFluxExt();
        assertTrue(path.contains(".flux"));
    }

    @Test
    public void shouldLoadUrlFilters() {
        List<String> filtersFromProp = unit.getUrlFilterClasses();
        List<String> streamOfFilters = Arrays.stream(UrlFiltersEnum.values()).map(UrlFiltersEnum::getClassPath).collect(Collectors.toList());
        Assertions.assertEquals(filtersFromProp,streamOfFilters);
    }

    @Test
    public void shouldLoadParseFilters() {
        List<String> filtersFromProp = unit.getParseFilterClasses();
        List<String> streamOfFilters = Arrays.stream(ParserFiltersEnum.values()).map(ParserFiltersEnum::getClassPath).collect(Collectors.toList());
        Assertions.assertEquals(filtersFromProp,streamOfFilters);
    }


    @Test
    public void shouldLoadStatusIndexSuffix() {
        String path = unit.getStatusIndexSuffix();
        assertTrue(path.contains("status"));
    }

    @Test
    public void shouldLoadMetricsIndexSuffix() {
        String path = unit.getMetricsIndexSuffix();
        assertTrue(path.contains("metrics"));
    }

    @Test
    public void shouldLoadContentIndexSuffix() {
        String path = unit.getContentIndexSuffix();
        assertTrue(path.contains("content"));
    }

    @Test
    public void shouldLoadConfigIndexSuffix() {
        String path = unit.getConfigIndexSuffix();
        assertTrue(path.contains("config"));
    }

    @Test
    public void shouldLoadStatusFile() {
        String path = unit.getStatusFile();
        assertTrue(path.contains("./elastic/status.json"));
    }

    @Test
    public void shouldLoadMetricsFile() {
        String path = unit.getMetricsFile();
        assertTrue(path.contains("./elastic/metrics.json"));
    }

    @Test
    public void shouldLoadContentFile() {
        String path = unit.getContentFile();
        assertTrue(path.contains("./elastic/content.json"));
    }

    @Test
    public void shouldLoadConfigFile() {
        String path = unit.getConfigFile();
        assertTrue(path.contains("./elastic/config.json"));
    }

    @Test
    public void shouldLoadStormMetricsTemplate() {
        String path = unit.getStormMetricsTemplate();
        assertTrue(path.contains("storm-metrics-template"));
    }

    @Test
    public void shouldLoadUrlFilterIndexId() {
        String path = unit.getUrlFilterIndexId();
        assertTrue(path.contains("es-urlfilters.json"));
    }

    @Test
    public void shouldLoadParseFilterIndexId() {
        String path = unit.getParseFilterIndexId();
        assertTrue(path.contains("es-parsefilters.json"));
    }

    @Test
    public void shouldLoadMaxSourcesBulkSize() {
        Integer num = unit.getMaxSourcesBulkSize();
        assertEquals(MAX_BULK_SIZE, (int) num);
    }


    @Test
    public void shouldLoadMaxElkSourcesBulkSize() {
        Integer num = unit.getMaxElkSourcesBulkSize();
        assertEquals(MAX_ELK_SOURCES_BULK_SIZE, (int) num);
    }

    @Test
    public void shouldLoadElkSourcesBulkSizeFactor() {
        Integer num = unit.getElkSourcesBulkSizeFactor();
        assertEquals(ELK_SOURCES_BULK_SIZE_FACTOR, (int) num);
    }

    @Test
    public void shouldLoadTopologyReportAddress() {
        String result = unit.getTopologyReportAddress();
        assertNotNull(result);
        assertEquals("host.docker.internal:8081/acquisitions/:id/report", result);
    }

}
