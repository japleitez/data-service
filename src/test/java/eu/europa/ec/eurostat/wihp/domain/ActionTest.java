package eu.europa.ec.eurostat.wihp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionAction;
import eu.europa.ec.eurostat.wihp.web.rest.TestUtil;
import java.time.Instant;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ActionTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    private static final Long DEFAULT_ID = 100L;
    private static final Instant DEFAULT_DATE = Instant.now();
    private static final Boolean DEFAULT_SUCCESS = true;
    private static final AcquisitionAction DEFAULT_ACTION = AcquisitionAction.PAUSE;

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
        TestUtil.equalsVerifier(Action.class);
        Action action1 = new Action();
        action1.setId(1L);
        Action action2 = new Action();
        action2.setId(action1.getId());
        assertThat(action1).isEqualTo(action2);
        action2.setId(2L);
        assertThat(action1).isNotEqualTo(action2);
        action1.setId(null);
        assertThat(action1).isNotEqualTo(action2);
    }

    public static Action createActions() {
        Action action = new Action().id(DEFAULT_ID).date(DEFAULT_DATE).action(DEFAULT_ACTION).success(DEFAULT_SUCCESS);
        return action;
    }

    @Test
    public void whenDateIsNull_IsInvalid() {
        //given
        Action action = createActions();
        action.setDate(null);

        //when
        Set<ConstraintViolation<Action>> violations = validator.validate(action);

        //then
        assertEquals(1, violations.size());
    }

    @Test
    public void whenActionIsNull_IsInvalid() {
        //given
        Action action = createActions();
        action.setAction(null);

        //when
        Set<ConstraintViolation<Action>> violations = validator.validate(action);

        //then
        assertEquals(1, violations.size());
    }

    @Test
    public void whenSuccessIsNull_IsInvalid() {
        //given
        Action action = createActions();
        action.setSuccess(null);

        //when
        Set<ConstraintViolation<Action>> violations = validator.validate(action);

        //then
        assertEquals(1, violations.size());
    }
}
