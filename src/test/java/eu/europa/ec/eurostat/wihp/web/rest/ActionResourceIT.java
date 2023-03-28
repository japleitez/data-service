package eu.europa.ec.eurostat.wihp.web.rest;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.domain.Acquisition;
import eu.europa.ec.eurostat.wihp.domain.Action;
import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionAction;
import eu.europa.ec.eurostat.wihp.repository.ActionRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ActionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ActionResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_SUCCESS = false;
    private static final Boolean UPDATED_SUCCESS = true;

    private static final AcquisitionAction DEFAULT_ACTION = AcquisitionAction.START;
    private static final AcquisitionAction UPDATED_ACTION = AcquisitionAction.PAUSE;

    private static final String ENTITY_API_URL = "/api/actions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ActionRepository actionsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restActionsMockMvc;

    private Action action;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Action createEntity(EntityManager em) {
        Action action = new Action().date(DEFAULT_DATE).success(DEFAULT_SUCCESS).action(DEFAULT_ACTION);
        Acquisition acquisition = AcquisitionResourceIT.createEntity(em);
        em.persist(acquisition);
        em.flush();
        action.setAcquisition(acquisition);
        return action;
    }

    public static Action buildEntity() {
        return new Action().date(DEFAULT_DATE).success(DEFAULT_SUCCESS).action(DEFAULT_ACTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Action createUpdatedEntity(EntityManager em) {
        Action action = new Action().date(UPDATED_DATE).success(UPDATED_SUCCESS).action(UPDATED_ACTION);
        return action;
    }

    @BeforeEach
    public void initTest() {
        action = createEntity(em);
    }

    @Test
    @Transactional
    void createActionsIsNotAllowed() throws Exception {
        restActionsMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Transactional
    void getAllActions() throws Exception {
        // Initialize the database
        actionsRepository.saveAndFlush(action);

        // Get all the actionsList
        restActionsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(action.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].success").value(hasItem(DEFAULT_SUCCESS.booleanValue())))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION.toString())));
    }

    @Test
    @Transactional
    void getActions() throws Exception {
        // Initialize the database
        actionsRepository.saveAndFlush(action);

        // Get the actions
        restActionsMockMvc
            .perform(get(ENTITY_API_URL_ID, action.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(action.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.success").value(DEFAULT_SUCCESS.booleanValue()))
            .andExpect(jsonPath("$.action").value(DEFAULT_ACTION.toString()));
    }

    @Test
    @Transactional
    void getNonExistingActions() throws Exception {
        // Get the actions
        restActionsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void updateActionWithPutIsNotAllowed() throws Exception {
        restActionsMockMvc
            .perform(put(ENTITY_API_URL_ID, action.getId()).with(csrf()).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Transactional
    void updateActionWithPatchIsNotAllowed() throws Exception {
        restActionsMockMvc
            .perform(patch(ENTITY_API_URL_ID, action.getId()).with(csrf()).contentType("application/merge-patch+json"))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Transactional
    void deleteActions() throws Exception {
        restActionsMockMvc
            .perform(delete(ENTITY_API_URL_ID, action.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isMethodNotAllowed());
    }
}
