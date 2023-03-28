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

public class UrlFilterTest {

    private static Validator validator;

    @BeforeAll
    public static void createValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UrlFilter.class);
        UrlFilter prlFilter1 = new UrlFilter();
        prlFilter1.setFilterId("on2");
        UrlFilter prlFilter2 = new UrlFilter();
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
        assertFalse(Pattern.matches(UrlFilter.CLASSNAME_PATTERN, invalidClassName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"some.test", "some.test.Filter", "some.TEST.Filter"})
    void className_testValid(String validClassName) {
        assertTrue(Pattern.matches(UrlFilter.CLASSNAME_PATTERN, validClassName));
    }

    @Test
    void validate_test_positive() {
        //given:
        UrlFilter urlFilter = createValidUrlFilter();
        //when:
        Set<ConstraintViolation<UrlFilter>> violations = validator.validate(urlFilter);
        //then:
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void validate_id_test_empty(String idValue) {
        //given:
        UrlFilter urlFilter = createValidUrlFilter();
        urlFilter.setFilterId(idValue);
        //when:
        Set<ConstraintViolation<UrlFilter>> violations = validator.validate(urlFilter);
        //then:
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"id,filter", "id;filter", "id.fil  ter", "  id.filter  ", "id.фильтр"})
    void validate_id_test_invalid(String idValue) {
        UrlFilter urlFilter = createValidUrlFilter();
        urlFilter.setFilterId(idValue);
        Set<ConstraintViolation<UrlFilter>> violations = validator.validate(urlFilter);
        assertFalse(violations.isEmpty());
    }

    private UrlFilter createValidUrlFilter() {
        return UrlFilter.builder().id(1L).filterId("filter.one").build();
    }
}
