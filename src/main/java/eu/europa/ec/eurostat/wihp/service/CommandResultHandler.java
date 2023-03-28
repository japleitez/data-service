package eu.europa.ec.eurostat.wihp.service;

import eu.europa.ec.eurostat.wihp.domain.Action;
import eu.europa.ec.eurostat.wihp.storm.command.CommandResult;

/**
 * Service Interface for handling the result after executing a Storm command
 */
public interface CommandResultHandler {
    void handle(Action action, CommandResult commandResult);
}
