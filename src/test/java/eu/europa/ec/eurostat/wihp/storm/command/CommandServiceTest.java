package eu.europa.ec.eurostat.wihp.storm.command;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import eu.europa.ec.eurostat.wihp.storm.command.CommandResult;
import eu.europa.ec.eurostat.wihp.storm.command.CommandService;
import org.junit.jupiter.api.Test;

public class CommandServiceTest {

    private final CommandService unit = new CommandService();

    @Test
    public void shouldReturnTrue_whenCommandExecutesSuccessfully() {
        String[] cmd = { "java", "--version" };

        //WHEN
        CommandResult result = unit.execute(cmd);
        assertNotNull(result.getInputStream());

        //THEN
        assertTrue(result.getSuccess());
    }

    @Test
    public void shouldReturnFalse_whenCommandReturnsError() throws IOException {
        //GIVEN
        String[] cmd = { "java", "--test" };

        //WHEN
        CommandResult result = unit.execute(cmd);

        //THEN
        assertFalse(result.getSuccess());
        assertNotNull(result.getInputStream());
        assertNotNull(result.getErrorStream());

        String execInputResult = new String(result.getInputStream().readAllBytes());
        assertTrue(execInputResult.isEmpty());
        String execErrorResult = new String(result.getErrorStream().readAllBytes());
        assertFalse(execErrorResult.isEmpty());
    }

    @Test
    public void shouldReturnFalse_whenCommandIsInvalid() {
        //GIVEN
        String[] cmd = { "invalid_command", "--test" };

        //WHEN
        CommandResult result = unit.execute(cmd);

        //THEN
        assertFalse(result.getSuccess());
        assertNull(result.getInputStream());
        assertNull(result.getErrorStream());
    }

    @Test
    public void shouldReturnFalse_whenCommandNull() {
        //GIVEN
        String[] cmd = null;

        //WHEN
        CommandResult result = unit.execute(cmd);

        //THEN
        assertFalse(result.getSuccess());
        assertNull(result.getInputStream());
    }

    @Test
    public void shouldReturnFalse_whenCommandEmpty() {
        //GIVEN
        String[] cmd = {};

        //WHEN
        CommandResult result = unit.execute(cmd);

        //THEN
        assertFalse(result.getSuccess());
        assertNull(result.getInputStream());
    }

    @Test
    public void shouldReturnProcessStreams_whenCommandValid() throws IOException {
        String[] cmd = { "java", "--version" };

        //WHEN
        CommandResult result = unit.execute(cmd);

        //THEN
        assertNotNull(result);
        assertNotNull(result.getInputStream());
        String execResult = new String(result.getInputStream().readAllBytes());
        assertNotNull(execResult);
        assertFalse(execResult.isEmpty());
    }
}
