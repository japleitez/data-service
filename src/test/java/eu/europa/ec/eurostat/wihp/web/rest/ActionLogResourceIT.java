package eu.europa.ec.eurostat.wihp.web.rest;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.domain.Action;
import eu.europa.ec.eurostat.wihp.domain.ActionLog;
import eu.europa.ec.eurostat.wihp.repository.ActionLogRepository;
import eu.europa.ec.eurostat.wihp.service.dto.ActionLogDTO;
import eu.europa.ec.eurostat.wihp.service.mapper.ActionLogMapper;
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
 * Integration tests for the {@link ActionLogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ActionLogResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_LOG_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_LOG_TEXT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/action-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ActionLogRepository actionLogRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restActionLogsMockMvc;

    private ActionLog actionLog;

    @Autowired
    private ActionLogMapper actionLogMapper;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ActionLog createEntity(EntityManager em) {
        ActionLog actionLog = new ActionLog().title(DEFAULT_TITLE).logText(DEFAULT_LOG_TEXT);
        Action action = ActionResourceIT.createEntity(em);
        action.addActionLogs(actionLog);
        em.persist(action);
        em.flush();
        return actionLog;
    }

    @BeforeEach
    public void initTest() {
        actionLog = createEntity(em);
    }

    @Test
    @Transactional
    void createActionLogsIsNotAllow() throws Exception {
        ActionLogDTO actionLogDTO = actionLogMapper.toDto(actionLog);
        restActionLogsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actionLogDTO))
            )
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Transactional
    void getAllActionLogs() throws Exception {
        // Initialize the database
        actionLogRepository.saveAndFlush(actionLog);

        // Get all the actionLogsList
        restActionLogsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(actionLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].logText").value(hasItem(DEFAULT_LOG_TEXT)));
    }

    @Test
    @Transactional
    void getActionLogs() throws Exception {
        // Initialize the database
        actionLogRepository.saveAndFlush(actionLog);

        // Get the actionLogs
        restActionLogsMockMvc
            .perform(get(ENTITY_API_URL_ID, actionLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(actionLog.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.logText").value(DEFAULT_LOG_TEXT));
    }

    @Test
    @Transactional
    void getNonExistingActionLogs() throws Exception {
        // Get the actionLogs
        restActionLogsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void updateActionLogsWithPutIsNotAllowed() throws Exception {
        restActionLogsMockMvc
            .perform(put(ENTITY_API_URL_ID, 100L).with(csrf()).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Transactional
    void updateActionLogsWithPatchIsNotAllowed() throws Exception {
        restActionLogsMockMvc
            .perform(patch(ENTITY_API_URL_ID, 100L).with(csrf()).contentType("application/merge-patch+json"))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Transactional
    void deleteActionLogsIsNotAllowed() throws Exception {
        restActionLogsMockMvc
            .perform(delete(ENTITY_API_URL_ID, actionLog.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isMethodNotAllowed());
    }
}
