package eu.europa.ec.eurostat.wihp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import eu.europa.ec.eurostat.wihp.web.rest.TestUtil;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ActionLogTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    private static final Long DEFAULT_ID = 100L;
    private static final String DEFAULT_TITLE = "DEFAULT TITLE";
    private static final String DEFAULT_LOG_TEXT = "DEFAULT LOG TEXT";

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
        TestUtil.equalsVerifier(ActionLog.class);
        ActionLog actionLog1 = new ActionLog();
        actionLog1.setId(1L);
        ActionLog actionLog2 = new ActionLog();
        actionLog2.setId(actionLog1.getId());
        assertThat(actionLog1).isEqualTo(actionLog2);
        actionLog2.setId(2L);
        assertThat(actionLog1).isNotEqualTo(actionLog2);
        actionLog1.setId(null);
        assertThat(actionLog1).isNotEqualTo(actionLog2);
    }

    public static ActionLog createActionLogs() {
        Action action = ActionTest.createActions();
        return new ActionLog().action(action).id(DEFAULT_ID).title(DEFAULT_TITLE).logText(DEFAULT_LOG_TEXT);
    }

    @Test
    public void whenTitleIsNull_IsInvalid() {
        //given
        ActionLog actionLog = createActionLogs();
        actionLog.setTitle(null);

        //when
        Set<ConstraintViolation<ActionLog>> violations = validator.validate(actionLog);

        //then
        assertEquals(1, violations.size());
    }

    @Test
    public void whenLogTextIsNull_IsInvalid() {
        //given
        ActionLog actionLog = createActionLogs();
        actionLog.setLogText(null);

        //when
        Set<ConstraintViolation<ActionLog>> violations = validator.validate(actionLog);

        //then
        assertEquals(1, violations.size());
    }
}
