package eu.europa.ec.eurostat.wihp.storm.command;

import java.io.InputStream;

public class CommandResult {

    private final InputStream inputStream;
    private final Boolean success;
    private final InputStream errorStream;

    public CommandResult(final InputStream inputStream, InputStream errorStream, final Boolean success) {
        this.inputStream = inputStream;
        this.errorStream = errorStream;
        this.success = success;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public Boolean getSuccess() {
        return success;
    }

    public InputStream getErrorStream() {
        return errorStream;
    }
}
