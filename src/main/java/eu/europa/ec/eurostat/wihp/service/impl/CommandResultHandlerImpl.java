package eu.europa.ec.eurostat.wihp.service.impl;

import eu.europa.ec.eurostat.wihp.domain.Action;
import eu.europa.ec.eurostat.wihp.domain.ActionLog;
import eu.europa.ec.eurostat.wihp.repository.ActionLogRepository;
import eu.europa.ec.eurostat.wihp.service.CommandResultHandler;
import eu.europa.ec.eurostat.wihp.storm.command.CommandResult;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
@Transactional
public class CommandResultHandlerImpl implements CommandResultHandler {

    private final ActionLogRepository actionLogRepository;

    public static final String SUCCESS_TITLE = "InputStream";
    public static final String FAILURE_TITLE = "ErrorStream";

    private static final String NULL_STRING = "null";
    private static final Logger LOG = LoggerFactory.getLogger(CommandResultHandlerImpl.class);

    public CommandResultHandlerImpl(ActionLogRepository actionLogRepository) {
        this.actionLogRepository = actionLogRepository;
    }

    @Override
    public void handle(Action action, CommandResult commandResult) {
        ActionLog actionLog;
        if (Boolean.TRUE.equals(commandResult.getSuccess())) {
            actionLog = new ActionLog(action, SUCCESS_TITLE, extractStringSafely(commandResult.getInputStream(), NULL_STRING));
        } else {
            actionLog = new ActionLog(action, FAILURE_TITLE, extractStringSafely(commandResult.getErrorStream(), NULL_STRING));
        }
        actionLogRepository.saveAndFlush(actionLog);
    }

    private String extractStringSafely(InputStream is, String defaultValue) {
        if (is == null) {
            return defaultValue;
        }
        String result;
        try {
            result = IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.error("Cannot copy InputStream to String", e);
            result = defaultValue;
        }
        return result;
    }
}
