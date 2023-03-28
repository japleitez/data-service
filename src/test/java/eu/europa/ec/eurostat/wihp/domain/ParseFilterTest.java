package eu.europa.ec.eurostat.wihp.domain;

import eu.europa.ec.eurostat.wihp.web.rest.TestUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParseFilterTest {

    private static Validator validator;

    @BeforeAll
    public static void createValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParseFilter.class);
        ParseFilter prlFilter1 = new ParseFilter();
        prlFilter1.setFilterId("one2");
        ParseFilter prlFilter2 = new ParseFilter();
        prlFilter2.setFilterId(prlFilter1.getFilterId());
        assertThat(prlFilter1).isEqualTo(prlFilter2);

        prlFilter2.setFilterId("two");
        assertThat(prlFilter1).isNotEqualTo(prlFilter2);

        prlFilter1.setFilterId(null);
        assertThat(prlFilter1).isNotEqualTo(prlFilter2);
    }

    @ParameterizedTest
    @ValueSource(strings = {"some.test.@", "some.test.aa,tt", "some.test.aa.t t", "some.test.aa.t;t", "some.test.aa.tt  ", "  ", ""})
    void className_testInvalid(String invalidClassName) {
        assertFalse(Pattern.matches(ParseFilter.CLASSNAME_PATTERN, invalidClassName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"some.test", "some.test.Filter", "some.TEST.Filter"})
    void className_testValid(String validClassName) {
        assertTrue(Pattern.matches(ParseFilter.CLASSNAME_PATTERN, validClassName));
    }

    @Test
    void validate_test_positive() {
        //given:
        ParseFilter ParseFilter = createValidUrlFilter();
        //when:
        Set<ConstraintViolation<ParseFilter>> violations = validator.validate(ParseFilter);
        //then:
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void validate_id_test_empty(String idValue) {
        ParseFilter ParseFilter = createValidUrlFilter();
        ParseFilter.setFilterId(idValue);

        Set<ConstraintViolation<ParseFilter>> violations = validator.validate(ParseFilter);

        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"id,filter", "id;filter", "id.fil  ter", "  id.filter  ", "id.фильтр"})
    void validate_id_test_invalid(String idValue) {
        ParseFilter ParseFilter = createValidUrlFilter();
        ParseFilter.setFilterId(idValue);
        Set<ConstraintViolation<ParseFilter>> violations = validator.validate(ParseFilter);
        assertFalse(violations.isEmpty());
    }

    private ParseFilter createValidUrlFilter() {
        return ParseFilter.builder().id(1L).filterId("filter.one").build();
    }
}
