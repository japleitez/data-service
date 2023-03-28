package eu.europa.ec.eurostat.wihp.service.crawlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mapstruct.ap.internal.util.Collections.asSet;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.wildfly.common.Assert.assertFalse;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.domain.ParseFilter;
import eu.europa.ec.eurostat.wihp.domain.ParserFilter;
import eu.europa.ec.eurostat.wihp.domain.UrlFilter;
import eu.europa.ec.eurostat.wihp.repository.CrawlerRepository;
import eu.europa.ec.eurostat.wihp.service.bulk.BulkSourcesService;
import eu.europa.ec.eurostat.wihp.service.dto.CrawlerDTO;
import eu.europa.ec.eurostat.wihp.service.dto.ParseFilterDTO;
import eu.europa.ec.eurostat.wihp.service.dto.ParserFilterDTO;
import eu.europa.ec.eurostat.wihp.service.dto.UrlFilterDTO;
import eu.europa.ec.eurostat.wihp.service.mapper.*;
import eu.europa.ec.eurostat.wihp.service.playground.PlaygroundValidationService;
import eu.europa.ec.eurostat.wihp.service.playground.model.ValidationFilterResult;
import eu.europa.ec.eurostat.wihp.service.validation.CrawlerImportServiceValidation;
import eu.europa.ec.eurostat.wihp.util.JsonNodeUtils;
import eu.europa.ec.eurostat.wihp.web.rest.errors.BadRequestAlertException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;

class CrawlerServiceValidationsTest {

    @Mock
    private CrawlerRepository mockCrawlerRepository;

    @Mock
    private BulkSourcesService mockBulkSourcesService;

    @Mock
    private CrawlerImportServiceValidation crawlerImportServiceValidation;

    private PlaygroundValidationService playgroundValidationService;

    private CrawlerService crawlerService;

    @BeforeEach
    public void initUpdateCrawlerService() {
        playgroundValidationService = mock(PlaygroundValidationService.class);

        MappersContainer mappersContainer = new MappersContainer(
            new CrawlerMapperImpl(),
            Mappers.getMapper(SourceMapper.class),
            new ParserFilterMapperImpl(),
            new UrlFilterMapperImpl(),
            new ParseFilterMapperImpl()
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
    void shouldMap_whenFiltersArePresent() {
        ParserFilter parserFilter = new ParserFilter();
        parserFilter.setId(1L);
        parserFilter.setName("name");

        ParserFilterDTO parserFilterDTO = new ParserFilterDTO();
        parserFilterDTO.setId(1L);
        final String expected = "another";
        parserFilterDTO.setName(expected);

        assertEquals("name", parserFilter.getName());

        crawlerService.map(asSet(parserFilter), asSet(parserFilterDTO));

        assertEquals(expected, parserFilter.getName());
    }

    @Test
    public void mapUrlFilter_test() {
        UrlFilter urlFilter = UrlFilter.builder().id(1L).filterId("name.filter").configuration(createJsonNode("some")).build();
        UrlFilterDTO urlFilterDTO = new UrlFilterDTO();
        urlFilterDTO.setFilterId("name.filter");
        urlFilterDTO.setConfiguration(createJsonNode("updated"));

        assertTrue(urlFilter.getConfiguration().toString().contains("some"));

        crawlerService.mapUrlFilter(asSet(urlFilter), asSet(urlFilterDTO));

        assertTrue(urlFilter.getConfiguration().toString().contains("updated"));
    }

    @Test
    public void mapWihpParseFilter_test() {
        ParseFilter parseFilter = ParseFilter.builder().id(1L).filterId("name.prs.filter").configuration(createJsonNode("some1")).build();
        ParseFilterDTO parseFilterDTO = new ParseFilterDTO();
        parseFilterDTO.setId(1L);
        parseFilterDTO.setFilterId("name.prs.filter");
        parseFilterDTO.setConfiguration(createJsonNode("updatedNow"));

        assertTrue(parseFilter.getConfiguration().toString().contains("some1"));

        crawlerService.mapWihpParseFilter(asSet(parseFilter), asSet(parseFilterDTO));

        assertTrue(parseFilter.getConfiguration().toString().contains("updatedNow"));
    }

    @Test
    public void checkDuplicatedCustomFilters_test_duplicated() {
        CrawlerDTO crawlerDTO = new CrawlerDTO();
        crawlerDTO.setUrlFilters(
            asSet(
                getUrlFilterDTO("f1", "c1"),
                getUrlFilterDTO("f1", "c2"),
                getUrlFilterDTO("f3", "c3"),
                getUrlFilterDTO("f2", "c4"),
                getUrlFilterDTO("f2", "c5"),
                getUrlFilterDTO("f4", "c6"),
                getUrlFilterDTO("f3", "c7")
            )
        );

        IllegalArgumentException thrown = Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> crawlerService.checkDuplicatedCustomFilters(crawlerDTO.getUrlFilters(), "urlFilters")
        );

        assertTrue(thrown.getMessage().contains("f1"));
        assertTrue(thrown.getMessage().contains("f2"));
        assertTrue(thrown.getMessage().contains("f3"));
        assertFalse(thrown.getMessage().contains("f4"));
    }

    @Test
    public void checkDuplicatedCustomFilters_test_no_duplicated() {
        CrawlerDTO crawlerDTO = new CrawlerDTO();
        crawlerDTO.setUrlFilters(
            asSet(
                getUrlFilterDTO("f1", "c1"),
                getUrlFilterDTO("f2", "c2"),
                getUrlFilterDTO("f3", "c2"),
                getUrlFilterDTO("f4", "c4"),
                getUrlFilterDTO("f5", "c5"),
                getUrlFilterDTO("f6", "c6"),
                getUrlFilterDTO("f7", "c7")
            )
        );
        crawlerService.checkDuplicatedCustomFilters(crawlerDTO.getUrlFilters(), "urlFilters");
    }

    @Test
    public void checkDuplicatedCustomFilters_test_no_duplicatedParseFilters() {
        CrawlerDTO crawlerDTO = new CrawlerDTO();
        crawlerDTO.setParseFilters(
            asSet(
                getParseFilterDTO("f1", "c1"),
                getParseFilterDTO("f2", "c2"),
                getParseFilterDTO("f3", "c2"),
                getParseFilterDTO("f4", "c4"),
                getParseFilterDTO("f5", "c5"),
                getParseFilterDTO("f6", "c6"),
                getParseFilterDTO("f7", "c7")
            )
        );
        crawlerService.checkDuplicatedCustomFilters(crawlerDTO.getParseFilters(), "parseFilters");
    }

    @Test
    public void checkDuplicatedCustomFilters_test_duplicatedParseFilters() {
        CrawlerDTO crawlerDTO = new CrawlerDTO();
        crawlerDTO.setParseFilters(
            asSet(
                getParseFilterDTO("f1", "c1"),
                getParseFilterDTO("f1", "c2"),
                getParseFilterDTO("f3", "c3"),
                getParseFilterDTO("f2", "c4"),
                getParseFilterDTO("f2", "c5"),
                getParseFilterDTO("f4", "c6"),
                getParseFilterDTO("f3", "c7")
            )
        );

        IllegalArgumentException thrown = Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> crawlerService.checkDuplicatedCustomFilters(crawlerDTO.getParseFilters(), "parseFilters")
        );

        assertTrue(thrown.getMessage().contains("f1"));
        assertTrue(thrown.getMessage().contains("f2"));
        assertTrue(thrown.getMessage().contains("f3"));
        assertFalse(thrown.getMessage().contains("f4"));
    }

    @Test
    public void hasValidationErrors_test() {
        assertFalse(crawlerService.hasValidationErrors(createValidationResult(1, 0)));
        assertFalse(crawlerService.hasValidationErrors(createValidationResult(4, 0)));
        assertFalse(crawlerService.hasValidationErrors(createValidationResult(0, 0)));

        assertTrue(crawlerService.hasValidationErrors(createValidationResult(1, 1)));
        assertTrue(crawlerService.hasValidationErrors(createValidationResult(4, 1)));

        assertEquals(4, createValidationResult(4, 1).getConfigurations().size());
        assertEquals("111", createValidationResult(4, 1).getConfigurations().get(0).getId());
        assertEquals(1, createValidationResult(4, 1).getConfigurations().get(0).getValidationErrors().size());
        assertEquals("1", createValidationResult(4, 1).getConfigurations().get(0).getValidationErrors().get(0).getId());
        assertEquals("ERROR", createValidationResult(4, 1).getConfigurations().get(0).getValidationErrors().get(0).getType());
    }

    @Test
    public void validateUrlFilters_test_duplicated() {
        CrawlerDTO crawlerDTO = new CrawlerDTO();
        crawlerDTO.setUrlFilters(asSet(getUrlFilterDTO("f1", "c1"), getUrlFilterDTO("f1", "c2")));
        IllegalArgumentException thrown = Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> crawlerService.validateUrlFilters(crawlerDTO)
        );
        assertTrue(thrown.getMessage().contains("f1"));
    }

    @Test
    public void validateUrlFilters_test_validation() {
        when(playgroundValidationService.validateUrlFilters(any(ArrayList.class))).thenReturn(getValidResult());
        CrawlerDTO crawlerDTO = new CrawlerDTO();
        crawlerDTO.setUrlFilters(asSet(getUrlFilterDTO("f1", "c1")));

        crawlerService.validateUrlFilters(crawlerDTO);
    }

    @Test
    public void validateUrlFilters_test_validationWithError() {
        when(playgroundValidationService.validateUrlFilters(any(ArrayList.class))).thenReturn(getNotValidResult());
        CrawlerDTO crawlerDTO = new CrawlerDTO();
        crawlerDTO.setUrlFilters(asSet(getUrlFilterDTO("f1", "c1")));

        BadRequestAlertException thrown = Assertions.assertThrows(
            BadRequestAlertException.class,
            () -> crawlerService.validateUrlFilters(crawlerDTO)
        );
        assertEquals("Input URL Filters are not valid", thrown.getMessage());
    }

    @Test
    public void validateParseFilters_test_validation() {
        when(playgroundValidationService.validateParseFilters(any(ArrayList.class))).thenReturn(getValidResult());
        CrawlerDTO crawlerDTO = new CrawlerDTO();
        crawlerDTO.setParseFilters(asSet(getParseFilterDTO("f1", "c1")));

        crawlerService.validateParseFilters(crawlerDTO);
    }

    @Test
    public void validateParseFilters_test_validationWithError() {
        when(playgroundValidationService.validateParseFilters(any(ArrayList.class))).thenReturn(getNotValidResult());
        CrawlerDTO crawlerDTO = new CrawlerDTO();
        crawlerDTO.setParseFilters(asSet(getParseFilterDTO("f1", "c1")));

        BadRequestAlertException thrown = Assertions.assertThrows(
            BadRequestAlertException.class,
            () -> crawlerService.validateParseFilters(crawlerDTO)
        );
        assertEquals("Input Parse Filters are not valid", thrown.getMessage());
    }

    private ValidationFilterResult getValidResult() {
        return createValidationResult(4, 0);
    }

    private ValidationFilterResult getNotValidResult() {
        return createValidationResult(4, 1);
    }

    private ValidationFilterResult createValidationResult(int configNumber, int errorNumber) {
        ValidationFilterResult val = new ValidationFilterResult();
        val.setConfigurations(createValidationConfigList(configNumber, errorNumber));
        return val;
    }

    private List<ValidationFilterResult.ValidationFilterResultConfiguration> createValidationConfigList(int configNumber, int errorNumber) {
        List<ValidationFilterResult.ValidationFilterResultConfiguration> list = new ArrayList<>();
        for (int i = 0; i < configNumber; i++) {
            list.add(createValidationConfiguration(errorNumber));
        }
        return list;
    }

    private ValidationFilterResult.ValidationFilterResultConfiguration createValidationConfiguration(int errorNumber) {
        ValidationFilterResult.ValidationFilterResultConfiguration validationConfiguration = new ValidationFilterResult.ValidationFilterResultConfiguration();
        validationConfiguration.setId("111");
        validationConfiguration.setValidationErrors(createValidationErrorList(errorNumber));
        return validationConfiguration;
    }

    private List<ValidationFilterResult.ValidationError> createValidationErrorList(int errors) {
        List<ValidationFilterResult.ValidationError> list = new ArrayList<>();
        for (int i = 0; i < errors; i++) {
            list.add(createValidationErrorList());
        }
        return list;
    }

    private ValidationFilterResult.ValidationError createValidationErrorList() {
        ValidationFilterResult.ValidationError validationError = new ValidationFilterResult.ValidationError();
        validationError.setValue("123");
        validationError.setId("1");
        validationError.setType("ERROR");
        return validationError;
    }

    private UrlFilterDTO getUrlFilterDTO(String filterId, String configValue) {
        UrlFilterDTO urlFIlterDto = new UrlFilterDTO();
        urlFIlterDto.setFilterId(filterId);
        urlFIlterDto.setConfiguration(createJsonNode(configValue));
        return urlFIlterDto;
    }

    private ParseFilterDTO getParseFilterDTO(String filterId, String configValue) {
        ParseFilterDTO parseFilterDTO = new ParseFilterDTO();
        parseFilterDTO.setFilterId(filterId);
        parseFilterDTO.setConfiguration(createJsonNode(configValue));
        return parseFilterDTO;
    }

    private JsonNode createJsonNode(String value) {
        return JsonNodeUtils.createJsonNode("{\"param1\":\"on1\", \"param2\":\"" + value + "\"}").orElseThrow();
    }
}
