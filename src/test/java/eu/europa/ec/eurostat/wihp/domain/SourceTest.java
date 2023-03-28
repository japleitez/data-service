package eu.europa.ec.eurostat.wihp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import eu.europa.ec.eurostat.wihp.web.rest.TestUtil;


import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.regex.Pattern;

class SourceTest {

    public static final String VALID_NAME = "John Potato";
    public static final String VALID_URL = "http://john.potato.com";
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
        TestUtil.equalsVerifier(Source.class);
        Source source1 = new Source();
        source1.setId(1L);
        Source source2 = new Source();
        source2.setId(source1.getId());
        assertThat(source1).isEqualTo(source2);
        source2.setId(2L);
        assertThat(source1).isNotEqualTo(source2);
        source1.setId(null);
        assertThat(source1).isNotEqualTo(source2);
    }

    @Test
    public void nameIsValidFormat() {
        boolean result = Pattern.matches(Source.NAME_REGEX, "aA9 _.");
        assertTrue(result);
    }

    @Test
    public void whenNameContainsNotAllowedSpecialCharacter_thenIsInvalid() {
        boolean result = Pattern.matches(Source.NAME_REGEX, "aA9$_.");
        Assert.assertFalse(result);
    }

    @Test
    public void whenNameLengthIsValid_thenSourceIsValid() {
        //given:
        Source source = new Source().name(VALID_NAME).url(VALID_URL);

        //when:
        Set<ConstraintViolation<Source>> violations = validator.validate(source);

        //then:
        assertTrue(violations.isEmpty());
    }

    @Test
    public void whenNameIsEmpty_thenSourceIsInvalid() {
        //given:
        Source source = new Source();

        source.setName("");
        source.setUrl(VALID_URL);

        //when:
        Set<ConstraintViolation<Source>> violations = validator.validate(source);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenNameHasDollar_thenSourceIsInvalid() {
        //given:
        Source source = new Source();

        source.setName("$");
        source.setUrl(VALID_URL);

        //when:
        Set<ConstraintViolation<Source>> violations = validator.validate(source);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenNameLengthIs100_thenIsValid() {
        //given:
        Source source = new Source();

        source.setName("a".repeat(100));
        source.setUrl(VALID_URL);

        //when:
        Set<ConstraintViolation<Source>> violations = validator.validate(source);

        //then:
        assertTrue(violations.isEmpty());
    }

    @Test
    public void whenNameLengthIs101_thenisInvalid() {
        //given:
        Source source = new Source();

        source.setName("a".repeat(101));
        source.setUrl(VALID_URL);

        //when:
        Set<ConstraintViolation<Source>> violations = validator.validate(source);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenNameIsNull_thenSourceIsInvalid() {
        //given:
        Source source = new Source();

        source.setName(null);
        source.setUrl(VALID_URL);

        //when:
        Set<ConstraintViolation<Source>> violations = validator.validate(source);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenUrlIsEmpty_thenSourceIsInvalid() {
        //given:
        Source source = new Source();

        source.setName(VALID_NAME);
        source.setUrl("");

        //when:
        Set<ConstraintViolation<Source>> violations = validator.validate(source);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenUrlIsNull_thenSourceIsInvalid() {
        //given:
        Source source = new Source();

        source.setName(VALID_NAME);
        source.setUrl(null);

        //when:
        Set<ConstraintViolation<Source>> violations = validator.validate(source);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenUrlIsInvalid_thenSourceIsInvalid() {
        //given:
        Source source = new Source();

        source.setName(VALID_NAME);
        source.setUrl("John Potato");

        //when:
        Set<ConstraintViolation<Source>> violations = validator.validate(source);

        //then:
        assertFalse(violations.isEmpty());
    }


}
