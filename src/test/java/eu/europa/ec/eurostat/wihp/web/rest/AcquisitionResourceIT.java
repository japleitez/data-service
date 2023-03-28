package eu.europa.ec.eurostat.wihp.web.rest;

import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.domain.Acquisition;
import eu.europa.ec.eurostat.wihp.domain.Action;
import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.domain.Report;
import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionAction;
import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionStatusEnum;
import eu.europa.ec.eurostat.wihp.repository.*;
import eu.europa.ec.eurostat.wihp.service.dto.CreateAcquisitionDTO;
import eu.europa.ec.eurostat.wihp.service.dto.StormReportDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link AcquisitionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
public class AcquisitionResourceIT {

    private static final UUID DEFAULT_ACQUISTION_ID = UUID.randomUUID();

    private static final AcquisitionStatusEnum DEFAULT_STATUS = AcquisitionStatusEnum.PROVISIONING;
    private static final AcquisitionStatusEnum UPDATED_STATUS = AcquisitionStatusEnum.QUEUED;

    private static final String ENTITY_API_URL = "/api/acquisitions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_API_URL_ID_ACTION = ENTITY_API_URL_ID + "/action/{action}";
    private static final String ENTITY_API_URL_ID_STATUS = ENTITY_API_URL_ID + "/report";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private AcquisitionRepository acquisitionRepository;

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private ActionLogRepository actionLogRepository;

    @Autowired
    private CrawlerRepository crawlerRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAcquisitionMockMvc;

    @Autowired
    private ReportRepository reportRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Acquisition createEntity(EntityManager em) {
        Acquisition acquisition = generateAcquisition(DEFAULT_ACQUISTION_ID);
        // Add required entity
        Crawler crawler;
        if (TestUtil.findAll(em, Crawler.class).isEmpty()) {
            crawler = CrawlerResourceIT.createEntity(em);
            em.persist(crawler);
            em.flush();
        } else {
            crawler = TestUtil.findAll(em, Crawler.class).get(0);
        }
        acquisition.setCrawler(crawler);
        return acquisition;
    }

    public static Acquisition generateAcquisition(UUID uuid) {
        return new Acquisition().workflowId(uuid).status(DEFAULT_STATUS).startDate(Instant.now()).lastUpdateDate(Instant.now());
    }

    public static Crawler createCrawler(EntityManager em) {
        Crawler crawler;
        if (TestUtil.findAll(em, Crawler.class).isEmpty()) {
            crawler = CrawlerResourceIT.createEntity(em);
            em.persist(crawler);
            em.flush();
        } else {
            crawler = TestUtil.findAll(em, Crawler.class).get(0);
        }
        return crawler;
    }

    public static CreateAcquisitionDTO createCreateAcquisitionDTO(String clrawlerName) {
        CreateAcquisitionDTO createAcquisitionDTO = new CreateAcquisitionDTO();
        createAcquisitionDTO.setUuid(DEFAULT_ACQUISTION_ID);
        createAcquisitionDTO.setName(clrawlerName);
        return createAcquisitionDTO;
    }

    @Test
    @Transactional
    public void whenGivenAcquisitionsThenGetCrawler() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        Crawler crawler;
        crawler = CrawlerResourceIT.createEntity(em);
        Acquisition acq1 = generateAcquisition(uuid1);
        Acquisition acq2 = generateAcquisition(uuid2);
        acq1.setCrawler(crawler);
        acq2.setCrawler(crawler);
        crawlerRepository.save(crawler);
        acquisitionRepository.save(acq1);
        acquisitionRepository.save(acq2);

        Optional<Acquisition> acq_1 = acquisitionRepository.findOneByWorkflowId(uuid1);
        Optional<Acquisition> acq_2 = acquisitionRepository.findOneByWorkflowId(uuid2);

        Optional<Crawler> optCrw = crawlerRepository.findOneByName(crawler.getName());

        Assertions.assertEquals(optCrw.orElseThrow().getName(), acq_1.orElseThrow().getCrawler().getName());
        Assertions.assertEquals(optCrw.orElseThrow().getName(), acq_2.orElseThrow().getCrawler().getName());
    }

    //@Test
    @Transactional
    void createAcquisition() throws Exception {
        Crawler crawler = createCrawler(em);
        CreateAcquisitionDTO createAcquisitionDTO = createCreateAcquisitionDTO(crawler.getName());

        int databaseSizeBeforeCreate = acquisitionRepository.findAll().size();
        // Create the Acquisition
        restAcquisitionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(createAcquisitionDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Acquisition in the database
        List<Acquisition> acquisitionList = acquisitionRepository.findAll();
        assertThat(acquisitionList).hasSize(databaseSizeBeforeCreate + 1);
        Acquisition testAcquisition = acquisitionList.get(acquisitionList.size() - 1);
        assertThat(testAcquisition.getWorkflowId()).isEqualTo(DEFAULT_ACQUISTION_ID);
        assertThat(testAcquisition.getStartDate()).isAfter(Instant.now().minusSeconds(60));
        assertThat(testAcquisition.getLastUpdateDate()).isAfter(Instant.now().minusSeconds(60));
        assertThat(testAcquisition.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void getAllAcquisitions() throws Exception {
        // Initialize the database
        Acquisition acquisition = createEntity(em);
        acquisitionRepository.saveAndFlush(acquisition);

        // Get all the acquisitionList
        restAcquisitionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(acquisition.getId().intValue())))
            .andExpect(jsonPath("$.[*].workflowId").value(hasItem(DEFAULT_ACQUISTION_ID.toString())))
            .andExpect(jsonPath("$.[*].startDate").exists())
            .andExpect(jsonPath("$.[*].lastUpdateDate").exists())
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getAcquisition() throws Exception {
        // Initialize the database

        Acquisition acquisition = createEntity(em);
        acquisitionRepository.saveAndFlush(acquisition);

        // Get the acquisition
        restAcquisitionMockMvc
            .perform(get(ENTITY_API_URL_ID, acquisition.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(acquisition.getId().intValue()))
            .andExpect(jsonPath("$.workflowId").value(DEFAULT_ACQUISTION_ID.toString()))
            .andExpect(jsonPath("$.startDate").exists())
            .andExpect(jsonPath("$.lastUpdateDate").exists())
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingAcquisition() throws Exception {
        // Get the acquisition
        restAcquisitionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void deleteAcquisition() throws Exception {
        // Initialize the database
        Acquisition acquisition = createEntity(em);
        acquisitionRepository.saveAndFlush(acquisition);

        int databaseSizeBeforeDelete = acquisitionRepository.findAll().size();

        // Delete the acquisition
        restAcquisitionMockMvc
            .perform(delete(ENTITY_API_URL_ID, acquisition.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Acquisition> acquisitionList = acquisitionRepository.findAll();
        assertThat(acquisitionList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    void whenAcquisitionIsUpdated_thenActionsRemainUntouched() {
        // Initialize the database
        Acquisition acquisition = createEntity(em);
        acquisitionRepository.saveAndFlush(acquisition);
        Long acquisitionId = acquisition.getId();

        Action action1 = ActionResourceIT.buildEntity().acquisition(acquisition);
        actionRepository.saveAndFlush(action1);

        // when change acquisition
        acquisition.updateStatus(AcquisitionStatusEnum.SUCCESS);
        acquisitionRepository.saveAndFlush(acquisition);

        // then actions are untouched
        Action a1 = actionRepository.getOne(action1.getId());
        Assertions.assertNotNull(a1);
        Assertions.assertEquals(acquisitionId, a1.getAcquisition().getId());
    }

    @Test
    @Transactional
    void whenExecuteAcquisitionAction_thenActionLogsArePresent() throws Exception {
        // Initialize the database
        Acquisition acquisition = createEntity(em);
        acquisitionRepository.saveAndFlush(acquisition);

        // Delete the acquisition
        restAcquisitionMockMvc.perform(post(ENTITY_API_URL_ID_ACTION, acquisition.getId(), AcquisitionAction.STOP));

        // then action logs are registered
        List<Action> actions = actionRepository.findAll();
        assertFalse(actions.isEmpty());
    }

    @Test
    @Transactional
    void whenReportingStatus_thenStatusIsUpdated() throws Exception {
        // Initialize the database
        Acquisition acquisition = createEntity(em);
        acquisitionRepository.saveAndFlush(acquisition);

        StormReportDTO report = new StormReportDTO()
            .stormId("stormId")
            .status(AcquisitionStatusEnum.STOPPED)
            .timestamp(Instant.now().toString());

        // Create the Acquisition
        restAcquisitionMockMvc
            .perform(
                post(ENTITY_API_URL_ID_STATUS, acquisition.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(report))
            )
            .andExpect(status().isOk());

        List<Report> reports = reportRepository.findAll();
        assertFalse(reports.isEmpty());
        Acquisition updated = reports.get(0).getAcquisition();
        assertEquals(acquisition.getId(), updated.getId());
        assertEquals(acquisition.getStatus(), updated.getStatus());
    }
}
