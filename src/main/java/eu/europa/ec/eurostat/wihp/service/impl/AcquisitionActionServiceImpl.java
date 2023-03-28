package eu.europa.ec.eurostat.wihp.service.impl;

import static java.util.Objects.nonNull;
import eu.europa.ec.eurostat.wihp.domain.Acquisition;
import eu.europa.ec.eurostat.wihp.domain.Action;
import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionAction;
import eu.europa.ec.eurostat.wihp.repository.AcquisitionRepository;
import eu.europa.ec.eurostat.wihp.repository.ActionRepository;
import eu.europa.ec.eurostat.wihp.service.AcquisitionActionService;
import eu.europa.ec.eurostat.wihp.service.CommandExecutorHandler;
import eu.europa.ec.eurostat.wihp.service.dto.ActionDTO;
import eu.europa.ec.eurostat.wihp.service.mapper.ActionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AcquisitionActionServiceImpl implements AcquisitionActionService {

    private final AcquisitionRepository acquisitionRepository;
    private final ActionRepository actionsRepository;
    private final CommandExecutorHandler commandHandler;
    private final ActionMapper actionMapper;

    public AcquisitionActionServiceImpl(
        AcquisitionRepository acquisitionRepository,
        ActionRepository actionsRepository,
        CommandExecutorHandler commandHandler,
        ActionMapper actionMapper
    ) {
        this.acquisitionRepository = acquisitionRepository;
        this.actionsRepository = actionsRepository;
        this.commandHandler = commandHandler;
        this.actionMapper = actionMapper;
    }

    @Override
    public ActionDTO execute(final Long acquisitionId, final AcquisitionAction actionToExecute) {
        return acquisitionRepository
            .findOne(acquisitionId)
            .map(acquisition -> updateStatusAndSave(acquisition, actionToExecute))
            .map(acquisition -> new Action(acquisition, actionToExecute))
            .map(this::executeAction)
            .orElseThrow(() -> new IllegalArgumentException("Acquisition does not exit, id:" + acquisitionId));
    }

    private Acquisition updateStatusAndSave(final Acquisition acquisition, final AcquisitionAction actionToExecute) {
        if (nonNull(actionToExecute.getAcquisitionStatus())) {
            acquisition.updateStatus(actionToExecute.getAcquisitionStatus());
            return acquisitionRepository.save(acquisition);
        }
        return acquisition;
    }

    protected ActionDTO executeAction(Action action) {
        AcquisitionAction actionToExecute = action.getAction();
        AcquisitionAction intermediateState = resolveIntermediateState(actionToExecute);
        action.setAction(intermediateState);
        action.setSuccess(true);
        actionsRepository.saveAndFlush(action);
        commandHandler.executeStormCommandAsync(action, actionToExecute);
        return actionMapper.toDto(action);
    }

    protected AcquisitionAction resolveIntermediateState(AcquisitionAction currentState) {
        switch (currentState) {
            case SUBMIT:
                return AcquisitionAction.SUBMITTING;
            case PAUSE:
                return AcquisitionAction.PAUSING;
            case START:
                return AcquisitionAction.STARTING;
            case STOP:
                return AcquisitionAction.STOPPING;
            default:
                throw new IllegalArgumentException("Acquisition State not implemented: " + currentState);
        }
    }
}
