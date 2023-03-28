package eu.europa.ec.eurostat.wihp.service.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import eu.europa.ec.eurostat.wihp.service.dto.ParserFilterDTO;
import eu.europa.ec.eurostat.wihp.util.JsonNodeUtils;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class ParserFilterValidatorTest {

    public static String VALID_REGEX_URL_FILTER =
        "{\"className\":\"com.digitalpebble.stormcrawler.filtering.regex.RegexURLFilter\",\"name\":\"RegexURLFilter\",\"params\":{\"urlFilters\":[\"-^(file|ftp|mailto):\",\"+.\"]}}";
    public static String INVALID_REGEX_URL_FILTER =
        "{\"className\":\"com.digitalpebble.stormcrawler.filtering.regex.RegexURLFilter\",\"name\":\"RegexURLFilter\",\"params\":{\"urlFilters\":[\"-^(file|ftp|mailto):\",\" \",\"\",null,\"+.\"]}}";

    private static String SELF_URL_FILTER =
        "{\"className\":\"com.digitalpebble.stormcrawler.filtering.basic.SelfURLFilter\",\"name\":\"SelfURLFilter\"}";
    private static String SITEMAP_FILTER =
        "{\"className\":\"com.digitalpebble.stormcrawler.filtering.sitemap.SitemapFilter\",\"name\":\"SitemapFilter\"}";

    private ParserFilterDTOValidator parserFilterDTOValidator = new ParserFilterDTOValidator();

    @Test
    public void verifyCollection_containsRegexUrlFilter() throws IOException {
        ParserFilterDTO dto1 = JsonNodeUtils.getObject(SELF_URL_FILTER, ParserFilterDTO.class);
        ParserFilterDTO dto2 = JsonNodeUtils.getObject(VALID_REGEX_URL_FILTER, ParserFilterDTO.class);
        Set<ParserFilterDTO> collection = new HashSet<>();
        collection.add(dto1);
        collection.add(dto2);

        Optional<ParserFilterDTO> optional = parserFilterDTOValidator.findParserFilterDTOForName(collection, "RegexURLFilter");

        assertTrue(optional.isPresent());
    }

    @Test
    public void verifyCollection_doesNotContainRegexUrlFilter() throws IOException {
        ParserFilterDTO dto1 = JsonNodeUtils.getObject(SELF_URL_FILTER, ParserFilterDTO.class);
        ParserFilterDTO dto2 = JsonNodeUtils.getObject(SITEMAP_FILTER, ParserFilterDTO.class);
        Set<ParserFilterDTO> collection = new HashSet<>();
        collection.add(dto1);
        collection.add(dto2);

        Optional<ParserFilterDTO> optional = parserFilterDTOValidator.findParserFilterDTOForName(collection, "RegexURLFilter");

        assertTrue(optional.isEmpty());
    }

    @Test
    public void verifyRegexUrlFilter_IsValid() throws IOException {
        ParserFilterDTO dto = JsonNodeUtils.getObject(VALID_REGEX_URL_FILTER, ParserFilterDTO.class);
        boolean isValid = parserFilterDTOValidator.isRegexURLFilterValid(dto);
        assertTrue(isValid);
    }

    @Test
    public void verifyRegexUrlFilter_IsNotValid() throws IOException {
        ParserFilterDTO dto = JsonNodeUtils.getObject(INVALID_REGEX_URL_FILTER, ParserFilterDTO.class);
        boolean isValid = parserFilterDTOValidator.isRegexURLFilterValid(dto);
        assertFalse(isValid);
    }
}
