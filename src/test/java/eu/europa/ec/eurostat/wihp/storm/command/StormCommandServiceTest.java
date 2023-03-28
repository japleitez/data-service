package eu.europa.ec.eurostat.wihp.storm.command;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import eu.europa.ec.eurostat.wihp.storm.command.CommandResult;
import eu.europa.ec.eurostat.wihp.storm.command.CommandService;
import eu.europa.ec.eurostat.wihp.storm.command.StormCommandService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@IntegrationTest
@ExtendWith(MockitoExtension.class)
public class StormCommandServiceTest {

    private StormCommandService unit;

    @Mock
    private CommandService mockCommandService;

    @Mock
    private ApplicationProperties mockApplicationProperties;

    @BeforeEach
    public void setUp() {

        unit = new StormCommandService(mockCommandService, mockApplicationProperties);
    }

    @Test
    public void shouldExecuteSubmitTopology() {
        final File config = new File("config/flux/all-crawler.flux");
        final String basicTopologyJar = "/basic-topology.jar";
        String[] expected = new String[] { "storm", "jar", basicTopologyJar, "org.apache.storm.flux.Flux", config.getAbsolutePath() };

        final CommandResult expectedResult = getCommandResult();
        when(mockCommandService.execute(eq(expected))).thenReturn(expectedResult);
        when(mockApplicationProperties.getTopologyPath()).thenReturn(basicTopologyJar);

        CommandResult commandResult = unit.submitTopology(config.getAbsolutePath());

        verify(mockApplicationProperties).getTopologyPath();
        Assertions.assertTrue(commandResult.getSuccess());
    }

    @Test
    public void whenFilePathNull_shouldThrowIllegalArgumentException() {
        assertThrows(NullPointerException.class, () -> unit.submitTopology(null));
    }

    @Test
    public void whenFilePathEmpty_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> unit.submitTopology(""));
    }

    @Test
    public void shouldExecutePauseTopology() {
        //given
        final String topologyName = "crawler_uuid_acquisitionId";
        when(mockApplicationProperties.getNimbusSeeds()).thenReturn(Arrays.asList("nimbus1", "nimbus2"));
        StormCommandService unit = Mockito.spy(new StormCommandService(mockCommandService,mockApplicationProperties));

        final CommandResult expectedResult = getCommandResult();
        String[] expected = new String[] { "storm", "deactivate", topologyName, "-c", "nimbus.seeds=[\"nimbus1\",\"nimbus2\"]" };
        Mockito.doReturn("\"nimbus1\",\"nimbus2\"").when(unit).getNimbusSeeds();
        when(mockCommandService.execute(eq(expected))).thenReturn(expectedResult);

        //when
        CommandResult commandResult = unit.pauseTopology(topologyName);

        //then
        Assertions.assertTrue(commandResult.getSuccess());
    }

    @Test
    public void shouldExecuteStartTopology() {
        //given
        final String topologyName = "crawler_uuid_acquisitionId";
        when(mockApplicationProperties.getNimbusSeeds()).thenReturn(Arrays.asList("nimbus1", "nimbus2"));
        StormCommandService unit = Mockito.spy(new StormCommandService(mockCommandService,mockApplicationProperties));

        final CommandResult expectedResult = getCommandResult();
        String[] expected = new String[] { "storm", "activate", topologyName, "-c", "nimbus.seeds=[\"nimbus1\",\"nimbus2\"]" };
        Mockito.doReturn("\"nimbus1\",\"nimbus2\"").when(unit).getNimbusSeeds();
        when(mockCommandService.execute(eq(expected))).thenReturn(expectedResult);

        //when
        CommandResult commandResult = unit.startTopology(topologyName);

        //then
        Assertions.assertTrue(commandResult.getSuccess());
    }

    @Test
    public void shouldExecuteStopTopology() {
        //given
        final String topologyName = "crawler_uuid_acquisitionId";
        when(mockApplicationProperties.getNimbusSeeds()).thenReturn(Arrays.asList("nimbus1", "nimbus2"));
        StormCommandService unit = Mockito.spy(new StormCommandService(mockCommandService,mockApplicationProperties));

        final CommandResult expectedResult = getCommandResult();
        String[] expected = new String[] { "storm", "kill", topologyName, "-w", "60", "-c", "nimbus.seeds=[\"nimbus1\",\"nimbus2\"]" };
        Mockito.doReturn("\"nimbus1\",\"nimbus2\"").when(unit).getNimbusSeeds();
        when(mockApplicationProperties.getKillTopologyWaitTime()).thenReturn("60");
        when(mockCommandService.execute(eq(expected))).thenReturn(expectedResult);

        //when
        CommandResult commandResult = unit.stopTopology(topologyName);

        //then
        Assertions.assertTrue(commandResult.getSuccess());
    }

    public static CommandResult getCommandResult() {
        return CommandResultFactory.buildCommandSuccess(
            new Process() {
                @Override
                public OutputStream getOutputStream() {
                    return null;
                }

                @Override
                public InputStream getInputStream() {
                    return new ByteArrayInputStream("input-stream".getBytes());
                }

                @Override
                public InputStream getErrorStream() {
                    return new ByteArrayInputStream("output-stream".getBytes());
                }

                @Override
                public int waitFor() {
                    return 0;
                }

                @Override
                public int exitValue() {
                    return 0;
                }

                @Override
                public void destroy() {}
            }
        );
    }
}
