package eu.europa.ec.eurostat.wihp.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import eu.europa.ec.eurostat.wihp.domain.ParserFilter;
import eu.europa.ec.eurostat.wihp.service.dto.ParserFilterDTO;
import eu.europa.ec.eurostat.wihp.service.validation.ParserFilterValidatorTest;
import eu.europa.ec.eurostat.wihp.util.JsonNodeUtils;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class ParserFilterMapperTest {

    private ParserFilterMapperImpl parserFilterMapperImpl = new ParserFilterMapperImpl();

    @Test
    void whenFilterDtoToFilter_thenFullEntityReturnedExceptId() throws IOException {
        ParserFilterMapper mapper = Mappers.getMapper(ParserFilterMapper.class);

        ParserFilterDTO dto = JsonNodeUtils.getObject(ParserFilterValidatorTest.VALID_REGEX_URL_FILTER, ParserFilterDTO.class);
        ParserFilter filter = new ParserFilter();
        mapper.filterDtoToFilter(dto, filter);

        Assertions.assertNull(filter.getId());
        Assertions.assertEquals(filter.getClassName(), dto.getClassName());
        Assertions.assertEquals(filter.getParams(), dto.getParams());
        Assertions.assertEquals(filter.getName(), dto.getName());
    }

    @Test
    void toEntity_test() {
        List<ParserFilter> list = parserFilterMapperImpl.toEntity(List.of(getParserFilterDTO(1L), getParserFilterDTO(2L)));

        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals(2L, list.get(1).getId());
    }

    @Test
    void toEntity_TestNullList() {
        List<ParserFilterDTO> dtoList = null;
        assertNull(parserFilterMapperImpl.toEntity(dtoList));
    }

    @Test
    void toDto_test() {
        List<ParserFilterDTO> list = parserFilterMapperImpl.toDto(List.of(getParserFilter(1L), getParserFilter(2L)));

        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals(2L, list.get(1).getId());
    }

    @Test
    void toDto_TestNullList() {
        List<ParserFilter> list = null;
        assertNull(parserFilterMapperImpl.toDto(list));
    }

    @Test
    void partialUpdate_test() {
        ParserFilter entity = new ParserFilter();
        ParserFilterDTO dto = getParserFilterDTO(11L);
        dto.setClassName("class11");
        dto.setName("name11");
        dto.setParams(JsonNodeUtils.createJsonNode("{\"param1\":\"on1\", \"param2\":\"1223\"}").orElseThrow());

        parserFilterMapperImpl.partialUpdate(entity, dto);

        assertEquals(11, entity.getId());
        assertEquals("class11", entity.getClassName());
        assertEquals("name11", entity.getName());
        assertEquals("on1", entity.getParams().get("param1").asText());
    }

    private ParserFilterDTO getParserFilterDTO(Long id) {
        ParserFilterDTO parserFilterDTO = new ParserFilterDTO();
        parserFilterDTO.setId(id);
        return parserFilterDTO;
    }

    private ParserFilter getParserFilter(Long id) {
        ParserFilter parserFilter = new ParserFilter();
        parserFilter.setId(id);
        return parserFilter;
    }
}
