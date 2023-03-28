package eu.europa.ec.eurostat.wihp.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import eu.europa.ec.eurostat.wihp.domain.UrlFilter;
import eu.europa.ec.eurostat.wihp.service.dto.UrlFilterDTO;
import eu.europa.ec.eurostat.wihp.util.JsonNodeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UrlFilterMapperTest {

    private static final String URL_FILTER_JSON = "{\"id\": \"urlfilters.test123\",\"configuration\": {\"maxPathRepetition\":8,\"maxLength\":888}}";
    private static final String URL_FILTER_JSON_INVALID_CONFIGURATION = "{\"id\": \"urlfilters.test123\",\"configuration\": {wrong string here}}";

    private UrlFilterMapper urlFilterMapper;

    @BeforeEach
    public void before() {
        urlFilterMapper = Mappers.getMapper(UrlFilterMapper.class);
    }

    @Test
    void mapUrlFilter_from_json() throws JsonProcessingException {
        UrlFilterDTO urlFilterDTO = JsonNodeUtils.getObject(URL_FILTER_JSON, UrlFilterDTO.class);

        assertEquals("urlfilters.test123", urlFilterDTO.getFilterId());
        assertEquals("{\"maxPathRepetition\":8,\"maxLength\":888}", urlFilterDTO.getConfiguration().toString());

        UrlFilter urlFilter = new UrlFilter();

        urlFilterMapper.urlFilterDtoToUrlFilter(urlFilterDTO, urlFilter);

        assertEquals(urlFilterDTO.getFilterId(), urlFilter.getFilterId());
        assertEquals(urlFilterDTO.getConfiguration(), urlFilter.getConfiguration());
    }

    @Test
    void mapUrlFilter_test_invalid_json() {
        JsonMappingException thrown =
            Assertions.assertThrows(JsonMappingException.class, () -> JsonNodeUtils.getObject(URL_FILTER_JSON_INVALID_CONFIGURATION, UrlFilterDTO.class));
        assertTrue(thrown.getMessage().contains("{wrong string here}"));
    }
}
