package eu.europa.ec.eurostat.wihp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import eu.europa.ec.eurostat.wihp.web.rest.TestUtil;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ConfigTest {

    public static final String VALID_NAME = "name";
    public static final String VALID_FILE = "file";

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
        TestUtil.equalsVerifier(Config.class);
        Config config1 = new Config();
        config1.setId(1L);
        Config config2 = new Config();
        config2.setId(config1.getId());
        assertThat(config1).isEqualTo(config2);
        config2.setId(2L);
        assertThat(config1).isNotEqualTo(config2);
        config1.setId(null);
        assertThat(config1).isNotEqualTo(config2);
    }

    @Test
    public void whenNameIsValid_thenConfigIsValid() {
        //given:
        Config config = new Config().name(VALID_NAME).file(VALID_FILE);

        //when:
        Set<ConstraintViolation<Config>> violations = validator.validate(config);

        //then:
        assertTrue(violations.isEmpty());
    }

    @Test
    public void whenNameIsNull_thenConfigIsInvalid() {
        //given:
        Config config = new Config();

        config.setName(null);
        config.setFile(VALID_FILE);

        //when:
        Set<ConstraintViolation<Config>> violations = validator.validate(config);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenNameIsEmpty_thenConfigIsInvalid() {
        //given:
        Config config = new Config();

        config.setName("");
        config.setFile(VALID_FILE);

        //when:
        Set<ConstraintViolation<Config>> violations = validator.validate(config);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenNameLengthIs101_thenConfigIsInvalid() {
        //given:
        Config config = new Config();

        config.setName("a".repeat(101));
        config.setFile(VALID_FILE);

        //when:
        Set<ConstraintViolation<Config>> violations = validator.validate(config);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenFileIsNull_thenConfigIsInvalid() {
        //given:
        Config config = new Config();

        config.setName(VALID_NAME);
        config.setFile(null);

        //when:
        Set<ConstraintViolation<Config>> violations = validator.validate(config);

        //then:
        assertFalse(violations.isEmpty());
    }

    @Test
    public void whenFileIsEmpty_thenConfigIsInvalid() {
        //given:
        Config config = new Config();

        config.setName(VALID_NAME);
        config.setFile("");

        //when:
        Set<ConstraintViolation<Config>> violations = validator.validate(config);

        //then:
        assertFalse(violations.isEmpty());
    }
}
