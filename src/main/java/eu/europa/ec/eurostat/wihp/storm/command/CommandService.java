package eu.europa.ec.eurostat.wihp.storm.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CommandService {

    private static final Logger LOG = LoggerFactory.getLogger(CommandService.class);

    public CommandResult execute(final String[] cmd) {
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(cmd);
            Process process = builder.start();
            return getCommandResult(process);
        } catch (InterruptedException ie) {
            LOG.error("InterruptedException while running command {}", cmd, ie);
            Thread.currentThread().interrupt();
            return CommandResultFactory.buildCommandError();
        } catch (Exception e) {
            LOG.error("Error while running command {}", cmd, e);
            return CommandResultFactory.buildCommandError();
        }
    }

    private CommandResult getCommandResult(final Process process) throws InterruptedException {
        if (process.waitFor() == 0) {
            return CommandResultFactory.buildCommandSuccess(process);
        }
        return CommandResultFactory.buildCommandError(process);
    }
}
