package eu.europa.ec.eurostat.wihp.service.crawlers;

import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.domain.ParseFilter;
import eu.europa.ec.eurostat.wihp.domain.UrlFilter;
import eu.europa.ec.eurostat.wihp.faker.ParserFilterDTOFaker;
import eu.europa.ec.eurostat.wihp.repository.CrawlerRepository;
import eu.europa.ec.eurostat.wihp.security.oauth2.AuthorizationHeaderUtil;
import eu.europa.ec.eurostat.wihp.service.bulk.BulkSourcesService;
import eu.europa.ec.eurostat.wihp.service.dto.*;
import eu.europa.ec.eurostat.wihp.service.mapper.*;
import eu.europa.ec.eurostat.wihp.service.playground.PlaygroundValidationService;
import eu.europa.ec.eurostat.wihp.service.validation.CrawlerImportServiceValidation;
import eu.europa.ec.eurostat.wihp.service.validation.ValidationResult;
import eu.europa.ec.eurostat.wihp.web.rest.CrawlerResourceIT;
import eu.europa.ec.eurostat.wihp.web.rest.SourceResourceIT;
import eu.europa.ec.eurostat.wihp.web.rest.errors.UnprocessableEntityException;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@IntegrationTest
public class CrawlerServiceIT {

    @Autowired
    CrawlerMapper crawlerMapper;
    @Autowired
    SourceMapper sourceMapper;
    @Autowired
    ParserFilterMapper parserFilterMapper;
    @Autowired
    UrlFilterMapper urlFilterMapper;
    @Autowired
    ParseFilterMapper parseFilterMapper;

    private CrawlerService crawlerService;

    @Mock
    private CrawlerImportServiceValidation crawlerImportServiceValidation;
    @Mock
    private BulkSourcesService mockBulkSourcesService;
    @Mock
    private CrawlerRepository mockCrawlerRepository;
    @Mock
    private AuthorizationHeaderUtil authorizationHeaderUtil;

    @BeforeEach
    public void setUp() {
        PlaygroundValidationService playgroundValidationService =
            new PlaygroundValidationService(new RestTemplateBuilder(), getAppProperties("playhost"), authorizationHeaderUtil);

        MappersContainer mappersContainer = new MappersContainer(crawlerMapper, sourceMapper, parserFilterMapper, urlFilterMapper, parseFilterMapper);

        crawlerService = new CrawlerService(
            mockCrawlerRepository,
            mockBulkSourcesService,
            playgroundValidationService,
            crawlerImportServiceValidation,
            mappersContainer);
    }

    @Test
    public void saveCrawlerTest() {

        String name = "job-crawler";
        CrawlerDTO crawlerDTO = crawlerMapper.toDto(CrawlerResourceIT.createCrawlerEntity());
        crawlerDTO.setName(name);
        List<SourceDTO> sources = sourceMapper.toDto(Lists.newArrayList(SourceResourceIT.createEntity(null)));
        sources.addAll(sourceMapper.toDto(Lists.newArrayList(SourceResourceIT.createEntity(null))));
        crawlerDTO.addSources(sources);

        Crawler crawler2 = crawlerMapper.toEntity(crawlerDTO);

        when(mockCrawlerRepository.save(any(Crawler.class))).thenReturn(crawler2);

        CrawlerDTO saved = crawlerService.saveCrawler(crawlerDTO, sources);

        assertEquals(2, saved.getSources().size());
        assertEquals(name, saved.getName());
        verify(mockCrawlerRepository).save(any(Crawler.class));
    }

    @Test
    public void crawlerMapperTest() {
        Crawler crawlerToSave = CrawlerResourceIT.createCrawlerEntity();
        crawlerToSave.getUrlFilters().add( UrlFilter.builder().id(1L).filterId("filter.one").crawler(crawlerToSave).build() );
        crawlerToSave.getParseFilters().add( ParseFilter.builder().id(11L).filterId("filterParse.one").crawler(crawlerToSave).build() );

        CrawlerDTO crawlerDTO = crawlerMapper.toDto(crawlerToSave);

        assertFalse(crawlerDTO.getUrlFilters().isEmpty());
        assertFalse(crawlerDTO.getParseFilters().isEmpty());

        List<UrlFilterDTO> filterDtoList = new ArrayList<>(crawlerDTO.getUrlFilters());
        assertEquals("filter.one", filterDtoList.get(0).getFilterId());
        assertEquals(1L, filterDtoList.get(0).getId());

        List<ParseFilterDTO> filterParseDtoList = new ArrayList<>(crawlerDTO.getParseFilters());
        assertEquals("filterParse.one", filterParseDtoList.get(0).getFilterId());
        assertEquals(11L, filterParseDtoList.get(0).getId());

        Crawler convertedCrawler = crawlerMapper.toEntity(crawlerDTO, Collections.emptyList());
        assertFalse(convertedCrawler.getUrlFilters().isEmpty());
        assertFalse(convertedCrawler.getParseFilters().isEmpty());

        List<UrlFilter> filterList = new ArrayList<>(convertedCrawler.getUrlFilters());
        assertEquals("filter.one", filterList.get(0).getFilterId());
        assertEquals(1L, filterList.get(0).getId());

        List<ParseFilter> filterParseList = new ArrayList<>(convertedCrawler.getParseFilters());
        assertEquals("filterParse.one", filterParseList.get(0).getFilterId());
        assertEquals(11L, filterParseList.get(0).getId());
    }

    @Test
    public void whenNoViolations_but_one_redundant_ThenReturnAlsoRedundant() {

        Crawler crawler1 = CrawlerResourceIT.createCrawlerEntity();
        CrawlerDTO crawlerDTO = crawlerMapper.toDto(crawler1);

        List<SourceDTO> sources1 = Lists.newArrayList(sourceMapper.toDto(SourceResourceIT.createEntity(null)));
        List<SourceDTO> sources2 = Lists.newArrayList(sourceMapper.toDto(SourceResourceIT.createEntity(null)));

        crawlerDTO.addSources(sources2);

        CrawlerResponseDTO response = new CrawlerResponseDTO();

        when(crawlerImportServiceValidation.validateSources(crawlerDTO)).thenReturn(new ValidationResult<>(new ArrayList<>(), new ArrayList<>()));
        when(crawlerImportServiceValidation.validateCrawler(crawlerDTO)).thenReturn(response);

        ValidationResult<SourceResponseDTO> violations = new ValidationResult<>(sourceMapper.fromListJsonDtoToListResponseDto(sources1), sourceMapper.fromListJsonDtoToListResponseDto(sources2));

        when(crawlerImportServiceValidation.splitRedundant(any(ArrayList.class))).thenReturn(violations);
        when(mockBulkSourcesService.saveSources(violations.getValidList())).thenReturn(sources2);

        sources2.addAll(sources1);
        crawlerDTO.getSources().clear();
        crawlerDTO.getSources().addAll(sources2);

        Crawler crawler = crawlerMapper.toEntity(crawlerDTO);
        when(mockCrawlerRepository.save(any(Crawler.class))).thenReturn(crawler);

        CrawlerDTO dto = crawlerService.persistRequest(crawlerDTO);

        verify(mockCrawlerRepository).save(any(Crawler.class));
        Assertions.assertTrue(dto.getSources().stream().anyMatch(s -> s.getName().contains(sources1.get(0).getName())));
        Assertions.assertTrue(dto.getSources().stream().anyMatch(s -> s.getName().contains(sources2.get(0).getName())));
    }

    @Test
    public void saveCrawlerWithParserFilters_throwsUnprocessableEntityException() {

        Crawler crawler = CrawlerResourceIT.createCrawlerEntity();
        CrawlerDTO crawlerDTO = crawlerMapper.toDto(CrawlerResourceIT.createCrawlerEntity());
        crawlerDTO.getParserFilters().add(ParserFilterDTOFaker.createParserFilterDTO("name1", "classname1"));
        crawlerDTO.getParserFilters().add(ParserFilterDTOFaker.createParserFilterDTO("name2", "classname2"));

        List<SourceDTO> sourceDTOS = sourceMapper.toDto(Lists.newArrayList(SourceResourceIT.createEntity(null)));
        sourceDTOS.addAll(sourceMapper.toDto(Lists.newArrayList(SourceResourceIT.createEntity(null))));
        crawlerDTO.addSources(sourceDTOS);

        Assertions.assertThrows(UnprocessableEntityException.class, () -> crawlerService.saveCrawler(crawlerDTO, sourceDTOS));

    }

    private ApplicationProperties getAppProperties(String hostName) {
        ApplicationProperties prop = new ApplicationProperties();
        prop.setPlaygroundServiceAddress(hostName);
        return prop;
    }
}
