package eu.europa.ec.eurostat.wihp.storm.command;

public class CommandResultFactory {

    private CommandResultFactory() {
    }
    
    public static CommandResult buildCommandError(final Process process) {
        return new CommandResult(process.getInputStream(), process.getErrorStream(), false);
    }

    public static CommandResult buildCommandSuccess(final Process process) {
        return new CommandResult(process.getInputStream(), process.getErrorStream(), true);
    }

    public static CommandResult buildCommandError() {
        return new CommandResult(null, null, false);
    }

}
