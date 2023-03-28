package eu.europa.ec.eurostat.wihp.service.crawlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.repository.CrawlerRepository;
import eu.europa.ec.eurostat.wihp.security.oauth2.AuthorizationHeaderUtil;
import eu.europa.ec.eurostat.wihp.service.bulk.BulkSourcesService;
import eu.europa.ec.eurostat.wihp.service.dto.CrawlerDTO;
import eu.europa.ec.eurostat.wihp.service.dto.CrawlerFieldResponseDTO;
import eu.europa.ec.eurostat.wihp.service.dto.CrawlerResponseDTO;
import eu.europa.ec.eurostat.wihp.service.mapper.*;
import eu.europa.ec.eurostat.wihp.service.playground.PlaygroundValidationService;
import eu.europa.ec.eurostat.wihp.service.validation.CrawlerImportServiceValidation;
import eu.europa.ec.eurostat.wihp.service.validation.ValidationResult;
import eu.europa.ec.eurostat.wihp.web.rest.CrawlerResourceIT;
import eu.europa.ec.eurostat.wihp.web.rest.errors.UnprocessableEntityException;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;

@ExtendWith(MockitoExtension.class)
public class CrawlerServiceTest {

    CrawlerMapper crawlerMapper = Mappers.getMapper(CrawlerMapper.class);
    SourceMapper sourceMapper = Mappers.getMapper(SourceMapper.class);

    @Mock
    private CrawlerImportServiceValidation crawlerImportServiceValidation;

    private CrawlerService crawlerService;

    @Mock
    private BulkSourcesService mockBulkSourcesService;

    @Mock
    private CrawlerRepository mockCrawlerRepository;

    @Autowired
    ParserFilterMapper parserFilterMapper;

    @Autowired
    UrlFilterMapper urlFilterMapper;

    @Autowired
    ParseFilterMapper parseFilterMapper;

    @Mock
    private AuthorizationHeaderUtil authorizationHeaderUtil;

    @BeforeEach
    public void setUp() {
        PlaygroundValidationService playgroundValidationService = new PlaygroundValidationService(
            new RestTemplateBuilder(),
            getAppProperties("test"),
            authorizationHeaderUtil
        );

        MappersContainer mappersContainer = new MappersContainer(
            crawlerMapper,
            sourceMapper,
            parserFilterMapper,
            urlFilterMapper,
            parseFilterMapper
        );

        crawlerService =
            new CrawlerService(
                mockCrawlerRepository,
                mockBulkSourcesService,
                playgroundValidationService,
                crawlerImportServiceValidation,
                mappersContainer
            );
    }

    @Test
    public void createCrawlerTest() {
        Long id = 1L;
        String name = "job-crawler";
        CrawlerDTO crawlerDTO = crawlerMapper.toDto(CrawlerResourceIT.createCrawlerEntity());
        crawlerDTO.setName(name);

        when(mockCrawlerRepository.save(any(Crawler.class)))
            .then(
                (Answer<Crawler>) invocation -> {
                    Crawler crawler = invocation.getArgument(0);
                    crawler.setId(1L);
                    return crawler;
                }
            );

        CrawlerDTO saved = crawlerService.createCrawler(crawlerDTO);

        assertEquals(id, saved.getId());
        assertEquals(name, saved.getName());
        verify(mockCrawlerRepository).save(any(Crawler.class));
    }

    @Test
    public void copyCrawlerTest() {
        String name = "name_";
        Long id = 1L;
        Long newId = 2L;
        when(mockCrawlerRepository.findById(eq(id))).thenReturn(Optional.of(CrawlerResourceIT.createCrawlerEntity()));
        when(mockCrawlerRepository.save(any(Crawler.class)))
            .then(
                (Answer<Crawler>) invocation -> {
                    Crawler crawler = invocation.getArgument(0);
                    crawler.setId(2L);
                    return crawler;
                }
            );

        Optional<CrawlerDTO> copy = crawlerService.copyCrawler(id, name);

        assertTrue(copy.isPresent());
        CrawlerDTO crawler = copy.get();
        assertEquals(name, crawler.getName());
        assertEquals(newId, crawler.getId());

        verify(mockCrawlerRepository).save(any(Crawler.class));
    }

    @Test
    public void whenValidateCrawlerReturnNotEmptyList_ThenThrow() {
        Crawler crawler1 = CrawlerResourceIT.createCrawlerEntity();
        CrawlerDTO crawlerDTO = crawlerMapper.toDto(crawler1);

        CrawlerResponseDTO response = new CrawlerResponseDTO();
        response.getFieldErrors().add(new CrawlerFieldResponseDTO().setObjectName("a"));
        when(crawlerImportServiceValidation.validateCrawler(crawlerDTO)).thenReturn(response);
        when(crawlerImportServiceValidation.validateSources(crawlerDTO)).thenReturn(new ValidationResult<>());

        Assertions.assertThrows(UnprocessableEntityException.class, () -> crawlerService.validateCrawler(crawlerDTO));
    }

    private ApplicationProperties getAppProperties(String hostName) {
        ApplicationProperties prop = new ApplicationProperties();
        prop.setPlaygroundServiceAddress(hostName);
        return prop;
    }
}
