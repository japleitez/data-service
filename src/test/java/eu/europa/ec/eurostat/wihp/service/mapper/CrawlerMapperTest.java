package eu.europa.ec.eurostat.wihp.service.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.domain.CrawlerUtils;
import eu.europa.ec.eurostat.wihp.domain.ParserFilter;
import eu.europa.ec.eurostat.wihp.service.dto.DynamicConfigDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SeleniumOptionsEnum;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static eu.europa.ec.eurostat.wihp.domain.CrawlerUtils.createCrawler;
import static org.junit.jupiter.api.Assertions.*;

public class CrawlerMapperTest {

    private CrawlerMapper mockCrawlerMapper = Mappers.getMapper(CrawlerMapper.class);

    @Test
    void givenAnyCrawler_thenMapperVerifiesRequirements() {
        CrawlerMapper mapper = Mappers.getMapper(CrawlerMapper.class);
        String newName = "copyName";
        Crawler existingCrawlerWithParserFilters = createCrawler();
        existingCrawlerWithParserFilters.addParserFilter(new ParserFilter().id(1L).name("NAME1").className("CLASSNAME1"));
        existingCrawlerWithParserFilters.addParserFilter(new ParserFilter().id(2L).name("NAME2").className("CLASSNAME2"));

        Crawler copy = mapper.copy(existingCrawlerWithParserFilters, newName);

        assertEquals(newName, copy.getName());
        assertEquals(existingCrawlerWithParserFilters.getSources().stream().findFirst().get().getId(), copy.getSources().stream().findFirst().get().getId());
        assertEquals(2, existingCrawlerWithParserFilters.getParserFilters().size());
        assertTrue(copy.getParserFilters().isEmpty());
    }

    @Test
    void testDefaultDynamicConfig() {
        DynamicConfigDTO defaultDto = new DynamicConfigDTO();

        assertEquals("de", defaultDto.getLanguage());
        assertTrue(defaultDto.isStartMaximized());
        assertEquals("1920,1080", defaultDto.getWindowSize());
        assertEquals(SeleniumOptionsEnum.ALLOW, defaultDto.getLoadImages());
        assertEquals(SeleniumOptionsEnum.BLOCK, defaultDto.getAllowCookies());
        assertEquals(SeleniumOptionsEnum.BLOCK, defaultDto.getAllowGeolocation());
    }

    @Test
    void testMapToJsonNode_fromDefaultDTO() {
        JsonNode jsonNode = mockCrawlerMapper.map(new DynamicConfigDTO());
        DynamicConfigDTO dto = CrawlerUtils.mapToDynamicConfigDTO(jsonNode);

        assertToDefaultDynamicConfigDTO(dto);
    }

    @Test
    void testMapToJsonNode_fromNullDTO() {
        DynamicConfigDTO nullDto = null;
        JsonNode jsonNode = mockCrawlerMapper.map(nullDto);
        DynamicConfigDTO dto = CrawlerUtils.mapToDynamicConfigDTO(jsonNode);

        assertToDefaultDynamicConfigDTO(dto);
    }

    @Test
    void testMapDynamicConfigDTO_fromJsonNode() {
        JsonNode jsonNode = mockCrawlerMapper.map(new DynamicConfigDTO());
        DynamicConfigDTO dto = mockCrawlerMapper.map(jsonNode);

        assertToDefaultDynamicConfigDTO(dto);
    }

    @Test
    void testMapDynamicConfigDTO_fromNullJsonNode() {
        JsonNode jsonNode = null;
        DynamicConfigDTO dto = mockCrawlerMapper.map(jsonNode);

        assertToDefaultDynamicConfigDTO(dto);
    }

    private void assertToDefaultDynamicConfigDTO(DynamicConfigDTO dto) {
        DynamicConfigDTO defaultDto = new DynamicConfigDTO();

        assertEquals(dto.getLanguage(), defaultDto.getLanguage());
        assertEquals(dto.isStartMaximized(), defaultDto.isStartMaximized());
        assertEquals(dto.getWindowSize(), defaultDto.getWindowSize());
        assertEquals(dto.getLoadImages(), defaultDto.getLoadImages());
        assertEquals(dto.getAllowCookies(), defaultDto.getAllowCookies());
        assertEquals(dto.getAllowGeolocation(), defaultDto.getAllowGeolocation());
    }

}
