package eu.europa.ec.eurostat.wihp.service;

import eu.europa.ec.eurostat.wihp.domain.Action;
import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionAction;

/**
 * Service Interface for handling the execution of a Storm command
 */
public interface CommandExecutorHandler {
    void executeStormCommandAsync(Action action, AcquisitionAction toExecute);
}
