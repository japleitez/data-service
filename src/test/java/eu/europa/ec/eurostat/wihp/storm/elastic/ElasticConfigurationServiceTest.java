package eu.europa.ec.eurostat.wihp.storm.elastic;

import static eu.europa.ec.eurostat.wihp.storm.configuration.CrawlerConfigurationService.DEFAULT_YML;
import static eu.europa.ec.eurostat.wihp.storm.elastic.ElasticConfigurationService.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.POJONode;
import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.config.properties.ApplicationPropertiesIT;
import eu.europa.ec.eurostat.wihp.domain.Acquisition;
import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.domain.Source;
import eu.europa.ec.eurostat.wihp.storm.configuration.JsonNodeService;
import eu.europa.ec.eurostat.wihp.storm.configuration.LoadFiltersService;
import eu.europa.ec.eurostat.wihp.storm.configuration.YmlService;
import eu.europa.ec.eurostat.wihp.web.rest.AcquisitionResourceIT;
import eu.europa.ec.eurostat.wihp.web.rest.CrawlerResourceIT;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;

@ExtendWith(MockitoExtension.class)
class ElasticConfigurationServiceTest {

    private ElasticConfigurationService unit;

    @Mock
    private JsonNodeService mockJsonNodeService;

    @Mock
    private IndexService mockIndexService;

    @Mock
    private LoadFiltersService mockLoadFiltersService;

    @Mock
    ElasticsearchProperties mockElasticsearchRestClientProperties;

    @Mock
    ApplicationProperties mockApplicationProperties;

    private JsonNode configuration;
    private final Acquisition acquisition = AcquisitionResourceIT.generateAcquisition(UUID.randomUUID());
    private final Crawler crawler = CrawlerResourceIT.createCrawlerEntity();

    @BeforeEach
    public void init() {
        unit =
            new ElasticConfigurationService(
                mockJsonNodeService,
                mockIndexService,
                mockLoadFiltersService,
                mockElasticsearchRestClientProperties,
                mockApplicationProperties
            );
        YmlService ymlReader = new YmlService();
        Optional<JsonNode> optNodeBefore = ymlReader.read(DEFAULT_YML);
        configuration = optNodeBefore.orElseThrow();
        acquisition.setCrawler(crawler);
    }

    @Test
    public void whenSetup() {
        Long id = 1L;
        acquisition.setId(id);
        when(mockLoadFiltersService.loadParseFilters(any())).thenReturn(Optional.of(new POJONode("")));
        when(mockLoadFiltersService.loadUrlFilters(any())).thenReturn(Optional.of(new POJONode("")));
        when(mockLoadFiltersService.loadWihpUrlFilters(any())).thenReturn(Optional.of(new POJONode("")));
        when(mockLoadFiltersService.loadWihpParseFilters(any())).thenReturn(Optional.of(new POJONode("")));

        when(mockApplicationProperties.getMaxElkSourcesBulkSize()).thenReturn(ApplicationPropertiesIT.MAX_ELK_SOURCES_BULK_SIZE);
        when(mockApplicationProperties.getElkSourcesBulkSizeFactor()).thenReturn(ApplicationPropertiesIT.ELK_SOURCES_BULK_SIZE_FACTOR);

        crawler.setSources(Set.of(new Source(), new Source()));

        unit.setup(configuration, acquisition);

        verify(mockIndexService).setupIndices(acquisition.getWorkflowId(), acquisition.getId());
        verify(mockIndexService).createParseFilter(any(), any(), any());
        verify(mockIndexService).createUrlFilter(any(), any(), any());
        // 2 times, 1 for each source
        verify(mockIndexService, times(2)).createStormSource(any(), any());
        verify(mockLoadFiltersService).loadParseFilters(eq(acquisition.getCrawler()));
        verify(mockLoadFiltersService).loadUrlFilters(eq(acquisition.getCrawler()));
        verify(mockElasticsearchRestClientProperties, times(4)).getUris();
        // 4 for index names and 4 for index addresses
        verify(mockJsonNodeService, times(8)).setProperty(any(), any(), any());
        verify(mockLoadFiltersService).applyParseFiltersConfigFile(any(), any(), any());
        verify(mockLoadFiltersService).applyUrlFiltersConfigFile(any(), any(), any());
    }

    @Test
    public void setElasticSearchConfigAddress() {
        final List<String> hosts = Arrays.asList("host1", "host2");
        when(mockElasticsearchRestClientProperties.getUris()).thenReturn(hosts);

        unit.updateElasticSearchConfigAddresses(configuration);

        verify(mockElasticsearchRestClientProperties).getUris();
        verify(mockJsonNodeService).setProperty(eq(configuration), eq(ES_CONFIG_INDEX_ADDRESSES), eq(hosts));
    }

    @Test
    public void setElasticSearchStatusAddress() {
        final List<String> hosts = Arrays.asList("host1", "host2");
        when(mockElasticsearchRestClientProperties.getUris()).thenReturn(hosts);

        unit.updateElasticSearchStatusAddresses(configuration);

        verify(mockElasticsearchRestClientProperties).getUris();
        verify(mockJsonNodeService).setProperty(eq(configuration), eq(ES_STATUS_INDEX_ADDRESSES), eq(hosts));
    }

    @Test
    public void setElasticSearchContentAddress() {
        final List<String> hosts = Arrays.asList("host1", "host2");
        when(mockElasticsearchRestClientProperties.getUris()).thenReturn(hosts);

        unit.updateElasticSearchContentAddresses(configuration);

        verify(mockElasticsearchRestClientProperties).getUris();
        verify(mockJsonNodeService).setProperty(eq(configuration), eq(ES_CONTENT_INDEX_ADDRESSES), eq(hosts));
    }

    @Test
    public void setElasticSearchMetricsAddress() {
        final List<String> hosts = Arrays.asList("host1", "host2");
        when(mockElasticsearchRestClientProperties.getUris()).thenReturn(hosts);

        unit.updateElasticSearchMetricsAddresses(configuration);

        verify(mockElasticsearchRestClientProperties).getUris();
        verify(mockJsonNodeService).setProperty(eq(configuration), eq(ES_METRICS_INDEX_ADDRESSES), eq(hosts));
    }
}
