package eu.europa.ec.eurostat.wihp.service.impl;

import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.domain.Acquisition;
import eu.europa.ec.eurostat.wihp.domain.Action;
import eu.europa.ec.eurostat.wihp.domain.ActionLog;
import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionAction;
import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionStatusEnum;
import eu.europa.ec.eurostat.wihp.repository.AcquisitionRepository;
import eu.europa.ec.eurostat.wihp.repository.ActionLogRepository;
import eu.europa.ec.eurostat.wihp.repository.ActionRepository;
import eu.europa.ec.eurostat.wihp.service.CommandExecutorHandler;
import eu.europa.ec.eurostat.wihp.service.CommandResultHandler;
import eu.europa.ec.eurostat.wihp.storm.command.CommandResult;
import eu.europa.ec.eurostat.wihp.storm.configuration.CrawlerConfigurationService;
import eu.europa.ec.eurostat.wihp.storm.command.StormCommandService;
import eu.europa.ec.eurostat.wihp.web.rest.AcquisitionResourceIT;
import java.io.File;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@ExtendWith(MockitoExtension.class)
class CommandExecutorHandlerImplTest {

    @Mock
    private StormCommandService stormCommandService;

    @Autowired
    ActionLogRepository actionLogRepository;

    @Autowired
    private CommandResultHandler commandResultHandler;

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private AcquisitionRepository acquisitionRepository;

    @Autowired
    private EntityManager em;

    @Mock
    private CrawlerConfigurationService crawlerConfigurationService;

    private CommandExecutorHandler commandExecutorHandler;

    @BeforeEach
    public void SetUp() {
        commandExecutorHandler =
            new CommandExecutorHandlerImpl(
                actionRepository,
                stormCommandService,
                commandResultHandler,
                crawlerConfigurationService,
                acquisitionRepository
            );
    }

    @Test
    @Transactional
    public void whenStormCommandIsExecuted_shallPersistResults() throws InterruptedException {
        // create action and its dependencies
        Acquisition acquisition = AcquisitionResourceIT.createEntity(em);
        acquisitionRepository.save(acquisition);
        acquisitionRepository.flush();
        Action action = new Action();
        action.setAcquisition(acquisition);
        action.setAction(AcquisitionAction.STARTING);
        AcquisitionAction actionToExecute = AcquisitionAction.START;

        // mock command
        CommandResult commandResult = new CommandResult(null, null, true);
        when(stormCommandService.startTopology(any())).thenReturn(commandResult);

        // execute action
        commandExecutorHandler.executeStormCommandAsync(action, actionToExecute);
        Thread.sleep(100);

        // check storm command Service was invoked
        verify(stormCommandService).startTopology(any());

        // check log saved and linked to action
        List<ActionLog> list = actionLogRepository.findAll();
        Assertions.assertFalse(list.isEmpty());
        ActionLog actionLog = list.get(0);
        Assertions.assertNotNull(actionLog);
        assertEquals(CommandResultHandlerImpl.SUCCESS_TITLE, actionLog.getTitle());
    }

    @Test
    @Transactional
    public void whenStormSubmitTopologyCommandIsExecuted_shallPersistResults() throws InterruptedException {
        // create action and its dependencies
        Acquisition acquisition = AcquisitionResourceIT.createEntity(em);
        acquisitionRepository.save(acquisition);
        acquisitionRepository.flush();
        Action action = new Action();
        action.setAcquisition(acquisition);
        action.setAction(AcquisitionAction.STARTING);
        AcquisitionAction actionToExecute = AcquisitionAction.SUBMIT;

        // mock command
        CommandResult commandResult = new CommandResult(null, null, true);
        when(stormCommandService.submitTopology(any())).thenReturn(commandResult);
        when(crawlerConfigurationService.createAcquisitionConfig(any())).thenReturn(of(new File("/")));

        // execute action
        commandExecutorHandler.executeStormCommandAsync(action, actionToExecute);
        Thread.sleep(100);

        // check storm command Service was invoked
        verify(stormCommandService).submitTopology(any());

        // check log saved and linked to action
        List<ActionLog> list = actionLogRepository.findAll();
        Assertions.assertFalse(list.isEmpty());
        ActionLog actionLog = list.get(0);
        Assertions.assertNotNull(actionLog);
        assertEquals(CommandResultHandlerImpl.SUCCESS_TITLE, actionLog.getTitle());
    }

    @Test
    @Transactional
    public void whenStormSubmitTopologyCommandAndCrawlerConfigServiceThrowsException_thenAcquisitionMarkedAsError()
        throws InterruptedException {
        // create action and its dependencies
        Acquisition acquisition = AcquisitionResourceIT.createEntity(em);
        acquisitionRepository.save(acquisition);
        acquisitionRepository.flush();
        Long acquisitionId = acquisition.getId();
        Action action = new Action();
        action.setAcquisition(acquisition);
        action.setAction(AcquisitionAction.STARTING);
        AcquisitionAction actionToExecute = AcquisitionAction.SUBMIT;

        // mock command
        CommandResult commandResult = new CommandResult(null, null, true);
        when(crawlerConfigurationService.createAcquisitionConfig(any())).thenThrow(RuntimeException.class);

        // execute action
        commandExecutorHandler.executeStormCommandAsync(action, actionToExecute);
        Thread.sleep(100);

        // check storm command Service was invoked
        verifyNoInteractions(stormCommandService);

        // check log saved and linked to action
        List<ActionLog> list = actionLogRepository.findAll();
        Assertions.assertFalse(list.isEmpty());
        ActionLog actionLog = list.get(0);
        Assertions.assertNotNull(actionLog);
        assertEquals(CommandResultHandlerImpl.FAILURE_TITLE, actionLog.getTitle());

        acquisition = acquisitionRepository.getOne(acquisitionId);
        assertEquals(AcquisitionStatusEnum.ERROR, acquisition.getStatus());
    }
}
