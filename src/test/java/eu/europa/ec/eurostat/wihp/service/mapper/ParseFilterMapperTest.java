package eu.europa.ec.eurostat.wihp.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import eu.europa.ec.eurostat.wihp.domain.ParseFilter;
import eu.europa.ec.eurostat.wihp.domain.ParserFilter;
import eu.europa.ec.eurostat.wihp.service.dto.ParseFilterDTO;
import eu.europa.ec.eurostat.wihp.service.dto.ParserFilterDTO;
import eu.europa.ec.eurostat.wihp.util.JsonNodeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParseFilterMapperTest {

    private static final String PARSE_FILTER_JSON = "{\"id\": \"parsefilters.test123\",\"configuration\": {\"maxPathRepetition\":8,\"maxLength\":888}}";
    private static final String PARSE_FILTER_JSON_INVALID_CONFIGURATION = "{\"id\": \"parsefilters.test123\",\"configuration\": {wrong string here}}";

    private ParseFilterMapper parseFilterMapper;
    private final ParseFilterMapperImpl parseFilterMapperImpl = new ParseFilterMapperImpl();

    @BeforeEach
    public void before() {
        parseFilterMapper = Mappers.getMapper(ParseFilterMapper.class);
    }

    @Test
    void mapParseFilter_from_json() throws JsonProcessingException {
        ParseFilterDTO parseFilterDTO = JsonNodeUtils.getObject(PARSE_FILTER_JSON, ParseFilterDTO.class);

        assertEquals("parsefilters.test123", parseFilterDTO.getFilterId());
        assertEquals("{\"maxPathRepetition\":8,\"maxLength\":888}", parseFilterDTO.getConfiguration().toString());

        ParseFilter parseFilter = new ParseFilter();

        parseFilterMapper.parseFilterDtoToParseFilter(parseFilterDTO, parseFilter);

        assertEquals(parseFilterDTO.getFilterId(), parseFilter.getFilterId());
        assertEquals(parseFilterDTO.getConfiguration(), parseFilter.getConfiguration());
        assertEquals(parseFilterDTO.getId(), parseFilter.getId());
    }

    @Test
    void mapParseFilter_test_invalid_json() {
        JsonMappingException thrown =
            Assertions.assertThrows(JsonMappingException.class, () -> JsonNodeUtils.getObject(PARSE_FILTER_JSON_INVALID_CONFIGURATION, ParseFilterDTO.class));
        assertTrue(thrown.getMessage().contains("{wrong string here}"));
    }

    @Test
    void toEntity_test(){
        List<ParseFilter> list =
        parseFilterMapperImpl.toEntity( List.of(getParseFilterDTO(1L), getParseFilterDTO(2L)) );

        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals(2L, list.get(1).getId());
    }

    @Test
    void toEntity_TestNullList(){
        List<ParseFilterDTO> dtoList = null;
        assertNull(parseFilterMapperImpl.toEntity(dtoList));
    }

    @Test
    void toDto_test(){
        List<ParseFilterDTO> list =
            parseFilterMapperImpl.toDto( List.of(getParseFilter(1L), getParseFilter(2L)) );

        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals(2L, list.get(1).getId());
    }

    @Test
    void toDto_TestNullList(){
        List<ParseFilter> list = null;
        assertNull(parseFilterMapperImpl.toDto(list));
    }

    @Test
    void partialUpdate_test(){
        ParseFilter entity = new ParseFilter();
        ParseFilterDTO dto = getParseFilterDTO(11L);
        dto.setFilterId("filter12");
        dto.setConfiguration(JsonNodeUtils.createJsonNode("{\"conf1\":\"on1\", \"conf2\":\"1223\"}").orElseThrow());

        parseFilterMapperImpl.partialUpdate(entity, dto);

        assertEquals(11, entity.getId());
        assertEquals("filter12", entity.getFilterId());
        assertEquals("on1", entity.getConfiguration().get("conf1").asText());
    }


    private ParseFilterDTO getParseFilterDTO(Long id){
        ParseFilterDTO parseFilterDTO = new ParseFilterDTO();
        parseFilterDTO.setId(id);
        return parseFilterDTO;
    }

    private ParseFilter getParseFilter(Long id){
        ParseFilter parseFilter = new ParseFilter();
        parseFilter.setId(id);
        return parseFilter;
    }

}
