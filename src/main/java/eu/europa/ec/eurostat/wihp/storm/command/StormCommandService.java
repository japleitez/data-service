package eu.europa.ec.eurostat.wihp.storm.command;

import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static org.apache.commons.lang3.Validate.notEmpty;

@Service
public class StormCommandService {

    private static final String SINGLE_QUOTE = "\"";
    private static final String STORM_COMMAND_NAME = "storm";
    private static final String NIMBUS_SEEDS_COMMAND_NAME = "nimbus.seeds=[";
    private final CommandService commandService;
    private final ApplicationProperties applicationProperties;
    private final String nimbusSeeds;

    public StormCommandService(final CommandService commandService, ApplicationProperties applicationProperties) {
        this.commandService = commandService;
        this.applicationProperties = applicationProperties;
        this.nimbusSeeds = buildNimbusSeeds();
    }

    public CommandResult submitTopology(final String configFileAbsolutePath) {
        notEmpty(configFileAbsolutePath, "The configFileAbsolutePath is null or empty");
        String[] command = new String[]{STORM_COMMAND_NAME, "jar", applicationProperties.getTopologyPath(),
            "org.apache.storm.flux.Flux", configFileAbsolutePath};
        return commandService.execute(command);
    }

    public CommandResult pauseTopology(final String topologyName) {
        String[] command = new String[]{STORM_COMMAND_NAME, "deactivate", topologyName, "-c",
            NIMBUS_SEEDS_COMMAND_NAME + getNimbusSeeds() + "]"};
        return commandService.execute(command);
    }

    public CommandResult startTopology(final String topologyName) {
        String[] command = new String[]{STORM_COMMAND_NAME, "activate", topologyName, "-c",
            NIMBUS_SEEDS_COMMAND_NAME + getNimbusSeeds() + "]"};
        return commandService.execute(command);
    }

    public CommandResult stopTopology(final String topologyName) {
        String[] command = new String[]{
            STORM_COMMAND_NAME,
            "kill",
            topologyName,
            "-w",
            applicationProperties.getKillTopologyWaitTime(),
            "-c",
            NIMBUS_SEEDS_COMMAND_NAME + getNimbusSeeds() + "]",
        };
        return commandService.execute(command);
    }

    private String buildNimbusSeeds() {
        return applicationProperties
            .getNimbusSeeds()
            .stream()
            .map(n -> SINGLE_QUOTE.concat(n).concat(SINGLE_QUOTE))
            .collect(Collectors.joining(","));
    }

    protected String getNimbusSeeds() {
        return this.nimbusSeeds;
    }

}
