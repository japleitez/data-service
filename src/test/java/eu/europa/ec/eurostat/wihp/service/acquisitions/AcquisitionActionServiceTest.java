package eu.europa.ec.eurostat.wihp.service.acquisitions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.domain.Acquisition;
import eu.europa.ec.eurostat.wihp.domain.Action;
import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionAction;
import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionStatusEnum;
import eu.europa.ec.eurostat.wihp.repository.AcquisitionRepository;
import eu.europa.ec.eurostat.wihp.repository.ActionRepository;
import eu.europa.ec.eurostat.wihp.service.AcquisitionActionService;
import eu.europa.ec.eurostat.wihp.service.CommandExecutorHandler;
import eu.europa.ec.eurostat.wihp.service.impl.AcquisitionActionServiceImpl;
import eu.europa.ec.eurostat.wihp.service.mapper.ActionMapper;
import eu.europa.ec.eurostat.wihp.web.rest.AcquisitionResourceIT;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@ExtendWith(MockitoExtension.class)
public class AcquisitionActionServiceTest {

    @Mock
    private CommandExecutorHandler commandHandler;

    @Autowired
    private AcquisitionRepository acquisitionRepository;

    @Autowired
    private ActionRepository actionsRepository;

    private AcquisitionActionService acquisitionActionService;

    @Autowired
    private EntityManager em;

    @Autowired
    private ActionMapper actionMapper;

    @BeforeEach
    public void setUp() {
        acquisitionActionService = new AcquisitionActionServiceImpl(acquisitionRepository, actionsRepository, commandHandler,actionMapper);
    }

    @Test
    @Transactional
    public void whenPauseIsExecuted_thenActionsIsPersisted() {
        Acquisition acquisition = AcquisitionResourceIT.createEntity(em);
        acquisitionRepository.save(acquisition);
        acquisitionRepository.flush();

        acquisitionActionService.execute(acquisition.getId(), AcquisitionAction.PAUSE);

        List<Action> actionList = actionsRepository.findAll();
        assertFalse(actionList.isEmpty());
        assertEquals(1, actionList.size());
        assertEquals(AcquisitionAction.PAUSING, actionList.get(0).getAction());
        verify(commandHandler).executeStormCommandAsync(any(), any());

        acquisition = acquisitionRepository.getOne(acquisition.getId());
        assertEquals(AcquisitionStatusEnum.PAUSING, acquisition.getStatus());
    }

    @Test
    @Transactional
    public void whenStartIsExecuted_thenActionsIsPersisted() {
        Acquisition acquisition = AcquisitionResourceIT.createEntity(em);
        acquisitionRepository.save(acquisition);
        acquisitionRepository.flush();

        acquisitionActionService.execute(acquisition.getId(), AcquisitionAction.START);

        List<Action> actionList = actionsRepository.findAll();
        assertFalse(actionList.isEmpty());
        assertEquals(1, actionList.size());
        assertEquals(AcquisitionAction.STARTING, actionList.get(0).getAction());
        verify(commandHandler).executeStormCommandAsync(any(), any());

        acquisition = acquisitionRepository.getOne(acquisition.getId());
        assertEquals(AcquisitionStatusEnum.STARTING, acquisition.getStatus());
    }

    @Test
    @Transactional
    public void whenStopIsExecuted_thenActionsIsPersisted() {
        Acquisition acquisition = AcquisitionResourceIT.createEntity(em);
        acquisitionRepository.save(acquisition);
        acquisitionRepository.flush();

        acquisitionActionService.execute(acquisition.getId(), AcquisitionAction.STOP);

        List<Action> actionList = actionsRepository.findAll();
        assertFalse(actionList.isEmpty());
        assertEquals(1, actionList.size());
        assertEquals(AcquisitionAction.STOPPING, actionList.get(0).getAction());
        verify(commandHandler).executeStormCommandAsync(any(), any());

        acquisition = acquisitionRepository.getOne(acquisition.getId());
        assertEquals(AcquisitionStatusEnum.STOPPING, acquisition.getStatus());
    }
}
