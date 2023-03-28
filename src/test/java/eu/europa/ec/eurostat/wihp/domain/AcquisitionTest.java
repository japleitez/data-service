package eu.europa.ec.eurostat.wihp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionStatusEnum;
import eu.europa.ec.eurostat.wihp.web.rest.TestUtil;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AcquisitionTest {

    private static final AcquisitionStatusEnum DEFAULT_STATUS = AcquisitionStatusEnum.PROVISIONING;
    private static final UUID DEFAULT_ACQUISTION_ID = UUID.randomUUID();
    private static final Instant DEFAULT_START_DATA = Instant.ofEpochMilli(0L);

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    private static Acquisition createValidAcquisitonInstance() {
        Acquisition acquisition = new Acquisition()
            .workflowId(DEFAULT_ACQUISTION_ID)
            .status(DEFAULT_STATUS)
            .startDate(DEFAULT_START_DATA)
            .lastUpdateDate(DEFAULT_START_DATA);
        // Add required entity
        Crawler crawler = CrawlerUtils.createCrawler();
        acquisition.setCrawler(crawler);
        return acquisition;
    }

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
        TestUtil.equalsVerifier(Acquisition.class);
        Acquisition acquisition1 = new Acquisition();
        acquisition1.setId(1L);
        Acquisition acquisition2 = new Acquisition();
        acquisition2.setId(acquisition1.getId());
        assertThat(acquisition1).isEqualTo(acquisition2);
        acquisition2.setId(2L);
        assertThat(acquisition1).isNotEqualTo(acquisition2);
        acquisition1.setId(null);
        assertThat(acquisition1).isNotEqualTo(acquisition2);
    }

    @Test
    public void whenInstanceIsValidIsNoViolations() {
        //given:
        Acquisition acquisition = createValidAcquisitonInstance();

        //when:
        Set<ConstraintViolation<Acquisition>> violations = validator.validate(acquisition);

        //then:
        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    public void whenWorkflowIdNull_theAcquisitionIsNotValid() {
        //given:
        Acquisition acquisition = createValidAcquisitonInstance();
        acquisition.setWorkflowId(null);

        //when:
        Set<ConstraintViolation<Acquisition>> violations = validator.validate(acquisition);

        //then:
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void whenStatusNull_theAcquisitionIsNotValid() {
        //given:
        Acquisition acquisition = createValidAcquisitonInstance();
        acquisition.updateStatus(null);

        //when:
        Set<ConstraintViolation<Acquisition>> violations = validator.validate(acquisition);

        //then:
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void whenStartDateNull_theAcquisitionIsNotValid() {
        //given:
        Acquisition acquisition = new Acquisition()
            .workflowId(DEFAULT_ACQUISTION_ID)
            .status(DEFAULT_STATUS)
            .startDate(null)
            .lastUpdateDate(DEFAULT_START_DATA);
        // Add required entity
        Crawler crawler = CrawlerUtils.createCrawler();
        acquisition.setCrawler(crawler);

        //when:
        Set<ConstraintViolation<Acquisition>> violations = validator.validate(acquisition);

        //then:
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void whenLastUpdateDateNull_theAcquisitionIsNotValid() {
        //given:
        Acquisition acquisition = createValidAcquisitonInstance();
        acquisition.setLastUpdateDate(null);
        //when:
        Set<ConstraintViolation<Acquisition>> violations = validator.validate(acquisition);

        //then:
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldGetTopologyName_whenCrawlerAndWorkflowExist() {
        Long acquisitionId = 123L;
        Acquisition acquisition = createValidAcquisitonInstance();
        acquisition.setId(acquisitionId);
        String expected = acquisition.getCrawler().getName() + "_" + acquisition.getWorkflowId() + "_" + acquisitionId;

        String topologyName = acquisition.getTopologyName();

        Assertions.assertEquals(expected, topologyName);
    }

    @Test
    public void shouldThrowException_whenCrawlerIsNull() {
        Long acquisitionId = 123L;
        Acquisition acquisition = createValidAcquisitonInstance();
        acquisition.setId(acquisitionId);
        acquisition.setCrawler(null);

        Assert.assertThrows(NullPointerException.class, acquisition::getTopologyName);
    }

    @Test
    public void shouldThrowException_whenCrawlerNameIsNull() {
        Long acquisitionId = 123L;
        Acquisition acquisition = createValidAcquisitonInstance();
        acquisition.setId(acquisitionId);
        acquisition.getCrawler().setName(null);

        Assert.assertThrows(NullPointerException.class, acquisition::getTopologyName);
    }

    @Test
    public void shouldThrowException_whenWorkflowIdIsNull() {
        Long acquisitionId = 123L;
        Acquisition acquisition = createValidAcquisitonInstance();
        acquisition.setWorkflowId(null);

        Assert.assertThrows(NullPointerException.class, acquisition::getTopologyName);
    }

    @Test
    public void shouldThrowException_whenIdIsNull() {
        Acquisition acquisition = createValidAcquisitonInstance();

        Assert.assertThrows(NullPointerException.class, acquisition::getTopologyName);
    }
}
