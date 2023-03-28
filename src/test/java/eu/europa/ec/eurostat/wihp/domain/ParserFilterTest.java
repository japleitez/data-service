package eu.europa.ec.eurostat.wihp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import eu.europa.ec.eurostat.wihp.web.rest.TestUtil;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ParserFilterTest {

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

    private ParserFilter createValidParseFilter() {
        ParserFilter parserFilter = new ParserFilter().id(1L).className("a.a.a.Valid").name("Valid");
        return parserFilter;
    }

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParserFilter.class);
        ParserFilter parserFilter1 = new ParserFilter();
        parserFilter1.setId(1L);
        ParserFilter parserFilter2 = new ParserFilter();
        parserFilter2.setId(parserFilter1.getId());
        assertThat(parserFilter1).isEqualTo(parserFilter2);
        parserFilter2.setId(2L);
        assertThat(parserFilter1).isNotEqualTo(parserFilter2);
        parserFilter1.setId(null);
        assertThat(parserFilter1).isNotEqualTo(parserFilter2);
    }

    @Test
    void whenClassNameIsNull_thenFilterIsInvalid() {
        //given:
        ParserFilter parserFilter = createValidParseFilter();
        parserFilter.setClassName(null);

        //when:
        Set<ConstraintViolation<ParserFilter>> violations = validator.validate(parserFilter);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenClassNameIsEmpty_thenFilterIsInvalid() {
        //given:
        ParserFilter parserFilter = createValidParseFilter();
        parserFilter.setClassName("");

        //when:
        Set<ConstraintViolation<ParserFilter>> violations = validator.validate(parserFilter);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenClassNameHasGoodFormat_thenFilterIsValid() {
        boolean result = Pattern.matches(ParserFilter.CLASSNAME_PATTERN, "a.a.a.Valid");
        assertTrue(result);
    }

    @Test
    void whenClassNameHasWrongFormat1_thenFilterIsInvalid() {
        boolean result = Pattern.matches(ParserFilter.CLASSNAME_PATTERN, "a.a.a.@AtSign");
        assertFalse(result);
    }

    @Test
    void whenClassNameHasWrongFormat2_thenFilterIsInvalid() {
        boolean result = Pattern.matches(ParserFilter.CLASSNAME_PATTERN, "a.a. .Space");
        assertFalse(result);
    }

    @Test
    void whenClassNameHasWrongFormat3_thenFilterIsInvalid() {
        boolean result = Pattern.matches(ParserFilter.CLASSNAME_PATTERN, "a.3.a.Number");
        assertFalse(result);
    }

    @Test
    void whenNameIsNull_thenFilterIsInvalid() {
        //given:
        ParserFilter parserFilter = createValidParseFilter();
        parserFilter.setName(null);

        //when:
        Set<ConstraintViolation<ParserFilter>> violations = validator.validate(parserFilter);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenNameIsEmpty_thenFilterIsInvalid() {
        //given:
        ParserFilter parserFilter = createValidParseFilter();
        parserFilter.setName("");

        //when:
        Set<ConstraintViolation<ParserFilter>> violations = validator.validate(parserFilter);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenNameIsTooLong_thenFilterIsInvalid() {
        //given:
        ParserFilter parserFilter = createValidParseFilter();
        parserFilter.setName("a".repeat(256));
        //when:
        Set<ConstraintViolation<ParserFilter>> violations = validator.validate(parserFilter);
        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenNameHasGoodFormat_thenFilterIsValid() {
        boolean result = Pattern.matches(ParserFilter.NAME_PATTERN, "Valid");
        assertTrue(result);
    }

    @Test
    void whenNameHasWrongFormat1_thenFilterIsInvalid() {
        boolean result = Pattern.matches(ParserFilter.NAME_PATTERN, "@AtSign");
        assertFalse(result);
    }

    @Test
    void whenNameHasWrongFormat2_thenFilterIsInvalid() {
        boolean result = Pattern.matches(ParserFilter.NAME_PATTERN, " Space");
        assertFalse(result);
    }

    @Test
    void whenNameHasWrongFormat3_thenFilterIsInvalid() {
        boolean result = Pattern.matches(ParserFilter.NAME_PATTERN, "3Number");
        assertFalse(result);
    }

    @Test
    void whenParamsIsNull_thenFilterIsValid() {
        //given:
        ParserFilter parserFilter = createValidParseFilter();
        parserFilter.setParams(null);

        //when:
        Set<ConstraintViolation<ParserFilter>> violations = validator.validate(parserFilter);

        //then:
        assertTrue(violations.isEmpty());
    }
}
