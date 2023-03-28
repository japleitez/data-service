package eu.europa.ec.eurostat.wihp.service.impl;

import eu.europa.ec.eurostat.wihp.domain.Acquisition;
import eu.europa.ec.eurostat.wihp.domain.Action;
import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionAction;
import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionStatusEnum;
import eu.europa.ec.eurostat.wihp.repository.AcquisitionRepository;
import eu.europa.ec.eurostat.wihp.repository.ActionRepository;
import eu.europa.ec.eurostat.wihp.service.CommandExecutorHandler;
import eu.europa.ec.eurostat.wihp.service.CommandResultHandler;
import eu.europa.ec.eurostat.wihp.storm.command.CommandResult;
import eu.europa.ec.eurostat.wihp.storm.command.CommandResultFactory;
import eu.europa.ec.eurostat.wihp.storm.configuration.CrawlerConfigurationService;
import eu.europa.ec.eurostat.wihp.storm.command.StormCommandService;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommandExecutorHandlerImpl implements CommandExecutorHandler {

    private static final Logger log = LoggerFactory.getLogger(CommandExecutorHandlerImpl.class);

    private final ActionRepository actionRepository;
    private final StormCommandService stormCommandService;
    private final CommandResultHandler commandResultHandler;
    private final CrawlerConfigurationService crawlerConfigurationService;
    private final AcquisitionRepository acquisitionRepository;

    public CommandExecutorHandlerImpl(
        final ActionRepository actionRepository,
        final StormCommandService stormCommandService,
        final CommandResultHandler commandResultHandler,
        final CrawlerConfigurationService crawlerConfigurationService,
        final AcquisitionRepository acquisitionRepository
    ) {
        this.actionRepository = actionRepository;
        this.stormCommandService = stormCommandService;
        this.commandResultHandler = commandResultHandler;
        this.crawlerConfigurationService = crawlerConfigurationService;
        this.acquisitionRepository = acquisitionRepository;
    }

    @Async
    @Override
    public void executeStormCommandAsync(Action action, AcquisitionAction toExecute) {
        String topologyName = action.getAcquisition().getTopologyName();
        CommandResult commandResult;
        switch (toExecute) {
            case PAUSE:
                commandResult = stormCommandService.pauseTopology(topologyName);
                break;
            case START:
                commandResult = stormCommandService.startTopology(topologyName);
                break;
            case STOP:
                commandResult = stormCommandService.stopTopology(topologyName);
                break;
            case SUBMIT:
                commandResult = submitTopology(action);
                break;
            default:
                throw new IllegalArgumentException("Acquisition Action not implemented: " + action.getAction());
        }
        action.setAction(toExecute);
        action.setSuccess(commandResult.getSuccess());
        actionRepository.saveAndFlush(action);
        commandResultHandler.handle(action, commandResult);
    }

    private CommandResult submitTopology(final Action action) {
        try {
            File config = crawlerConfigurationService.createAcquisitionConfig(action.getAcquisition()).orElseThrow();
            CommandResult result = stormCommandService.submitTopology(config.getAbsolutePath());
            if (!Boolean.TRUE.equals(result.getSuccess())) {
                log.error("Error while submitting topology action : {}, config path: {}", action.getAction(), config.getAbsolutePath());
                handleAcquisitionSubmissionError(action);
            }
            return result;
        } catch (Exception e) {
            log.error("Unexpected Exception while submitting topology", e);
            handleAcquisitionSubmissionError(action);
            return CommandResultFactory.buildCommandError();
        }
    }

    private void handleAcquisitionSubmissionError(final Action action) {
        Acquisition acquisition = action.getAcquisition();
        acquisition.updateStatus(AcquisitionStatusEnum.ERROR);
        acquisitionRepository.saveAndFlush(acquisition);
    }
}
