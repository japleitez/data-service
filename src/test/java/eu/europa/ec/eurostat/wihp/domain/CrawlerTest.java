package eu.europa.ec.eurostat.wihp.domain;

import eu.europa.ec.eurostat.wihp.service.mapper.CrawlerMapper;
import eu.europa.ec.eurostat.wihp.web.rest.TestUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

import static eu.europa.ec.eurostat.wihp.domain.CrawlerUtils.createCrawler;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CrawlerTest {

    public static final Integer NAME_MAX_LENGTH = 100;
    public static final Integer MINUS_ONE = -1;
    public static final Integer MAX_INTEGER = 2147483647;
    public static final Integer ONE_YEAR_IN_MINUTES = 525600;

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    public static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    public static void close() {
        validatorFactory.close();
    }

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Crawler.class);
        Crawler crawler1 = new Crawler();
        crawler1.setId(1L);
        Crawler crawler2 = new Crawler();
        crawler2.setId(crawler1.getId());
        assertThat(crawler1).isEqualTo(crawler2);
        crawler2.setId(2L);
        assertThat(crawler1).isNotEqualTo(crawler2);
        crawler1.setId(null);
        assertThat(crawler1).isNotEqualTo(crawler2);
    }

    @Test
    public void whenNameRegExpAllowedCharacter_thenIsValid() {
        assertTrue(Pattern.matches(Crawler.NAME_REGEX, "aA0_-"));
        assertTrue(Pattern.matches(Crawler.NAME_REGEX, "abc-abc"));
        assertFalse(Pattern.matches(Crawler.NAME_REGEX, "abc abc"));
        assertFalse(Pattern.matches(Crawler.NAME_REGEX, "abc.abc"));
        assertFalse(Pattern.matches(Crawler.NAME_REGEX, "aA0_-$"));
    }

    @Test
    public void whenNameLengthIsValid_thenCrawlerIsValid() {
        //given:
        Crawler crawler = createCrawler();

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertTrue(violations.isEmpty());
    }

    @Test
    public void whenNameIsNull_thenCrawlerIsInvalid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setName(null);

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenNameIsEmpty_thenCrawlerIsInvalid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setName("");

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenNameHasDollar_thenCrawlerIsInvalid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setName("$");

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenNameLengthIsMaxLength_thenCrawlerIsValid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setName("a".repeat(NAME_MAX_LENGTH));

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertTrue(violations.isEmpty());
    }

    @Test
    public void whenNameLengthIsAboveMaxLength_thenCrawlerIsInvalid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setName("a".repeat(NAME_MAX_LENGTH + 1));

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenFetchIntervalIsNull_thenCrawlerIsInvalid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setFetchInterval(null);

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenFetchIntervalIsBelowMin_thenCrawlerIsInvalid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setFetchInterval(MINUS_ONE - 1);

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenFetchIntervalIsAboveMax_thenCrawlerIsInvalid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setFetchInterval(ONE_YEAR_IN_MINUTES + 1);

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenFetchIntervalWhenErrorIsNull_thenCrawlerIsInvalid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setFetchIntervalWhenError(null);

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenFetchIntervalWhenErrorIsBelowMin_thenCrawlerIsInvalid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setFetchIntervalWhenError(MINUS_ONE - 1);

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenFetchIntervalWhenErrorIsAboveMax_thenCrawlerIsInvalid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setFetchIntervalWhenError(ONE_YEAR_IN_MINUTES + 1);

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenFetchIntervalWhenFetchErrorIsNull_thenCrawlerIsInvalid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setFetchIntervalWhenFetchError(null);

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenFetchIntervalWhenFetchErrorIsBelowMin_thenCrawlerIsInvalid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setFetchIntervalWhenFetchError(MINUS_ONE - 1);

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenFetchIntervalWhenFetchErrorIsAboveMax_thenCrawlerIsInvalid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setFetchIntervalWhenFetchError(ONE_YEAR_IN_MINUTES + 1);

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenExtractorNoTextIsNull_thenCrawlerIsInvalid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setExtractorNoText(null);

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenHttpContentLimitIsNull_thenCrawlerIsInvalid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setHttpContentLimit(null);

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenHttpContentLimitIsBellowMin_thenCrawlerIsInvalid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setHttpContentLimit(MINUS_ONE - 1);

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenHttpContentLimitIsAboveMax_thenCrawlerIsInvalid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setHttpContentLimit(MAX_INTEGER + 1);

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenEmitOutLinksIsNull_thenCrawlerIsInvalid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setEmitOutLinks(null);

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenMaxEmitOutLinksPerPageIsNull_thenCrawlerIsInvalid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setMaxEmitOutLinksPerPage(null);

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenMaxEmitOutLinksPerPageIsBellowMin_thenCrawlerIsInvalid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setMaxEmitOutLinksPerPage(MINUS_ONE - 1);

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenMaxEmitOutLinksPerPageIsAboveMax_thenCrawlerIsInvalid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setMaxEmitOutLinksPerPage(MAX_INTEGER + 1);

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void copyCrawlerTest() {
        //given:
        Crawler existingCrawlerWithParserFilters = createCrawler();
        existingCrawlerWithParserFilters.addParserFilter(new ParserFilter().id(1L).name("NAME1").className("CLASSNAME1"));
        existingCrawlerWithParserFilters.addParserFilter(new ParserFilter().id(2L).name("NAME2").className("CLASSNAME2"));

        //when:
        String name = "random";

        Crawler copy = Mappers.
            getMapper(CrawlerMapper.class)
            .copy(existingCrawlerWithParserFilters, name);

        //then:
        assertThat(name).isEqualTo(copy.getName());
        assertThat(existingCrawlerWithParserFilters.getFetchInterval()).isEqualTo(copy.getFetchInterval());
        assertThat(existingCrawlerWithParserFilters.getFetchIntervalWhenError()).isEqualTo(copy.getFetchIntervalWhenError());
        assertThat(existingCrawlerWithParserFilters.getFetchIntervalWhenFetchError()).isEqualTo(copy.getFetchIntervalWhenFetchError());
        assertThat(existingCrawlerWithParserFilters.getExtractorNoText()).isEqualTo(copy.getExtractorNoText());
        assertThat(existingCrawlerWithParserFilters.getExtractorTextIncludePattern()).isEqualTo(copy.getExtractorTextIncludePattern());
        assertThat(existingCrawlerWithParserFilters.getExtractorTextExcludeTags()).isEqualTo(copy.getExtractorTextExcludeTags());
        assertThat(existingCrawlerWithParserFilters.getHttpContentLimit()).isEqualTo(copy.getHttpContentLimit());
        assertThat(existingCrawlerWithParserFilters.getEmitOutLinks()).isEqualTo(copy.getEmitOutLinks());
        assertThat(existingCrawlerWithParserFilters.getMaxEmitOutLinksPerPage()).isEqualTo(copy.getMaxEmitOutLinksPerPage());

        assertThat(existingCrawlerWithParserFilters.getSources().size()).isEqualTo(copy.getSources().size());
        assertEquals(2, existingCrawlerWithParserFilters.getParserFilters().size());
        assertTrue(copy.getParserFilters().isEmpty());
    }

    @Test
    public void whenDynamicIsNull_thenCrawlerIsInvalid() {
        //given:
        Crawler crawler = createCrawler();

        crawler.setDynamic(null);

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenDynamicConfigIsNull_thenCrawlerIsValid() {
        //given:
        Crawler crawler = createCrawler();
        crawler.setDynamicConfig(null);

        //when:
        Set<ConstraintViolation<Crawler>> violations = validator.validate(crawler);

        //then:
        assertTrue(violations.isEmpty());
    }

    @Test
    public void setAcquisitions_test(){
        Crawler crawler = createCrawler();
        assertTrue(crawler.getAcquisitions().isEmpty());

        Crawler crawlerRes = crawler.acquisitions(Collections.singleton(new Acquisition().id(1L)));
        assertEquals(1, crawlerRes.getAcquisitions().size());
        assertEquals(1L, crawlerRes.getAcquisitions().iterator().next().getId());

        crawlerRes = crawler.acquisitions(Collections.singleton(new Acquisition().id(2L)));
        assertEquals(1, crawlerRes.getAcquisitions().size());
        assertEquals(2L, crawlerRes.getAcquisitions().iterator().next().getId());
    }
}
