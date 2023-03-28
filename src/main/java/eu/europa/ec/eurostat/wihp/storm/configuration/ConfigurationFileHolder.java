package eu.europa.ec.eurostat.wihp.storm.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextException;

public class ConfigurationFileHolder {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationFileHolder.class);

    public Optional<File> save(Long acquisitionId, JsonNode configuration, String path, String extension) {
        verifyInput(acquisitionId, configuration);
        try {
            return Optional.of(saveJsonNodeToFile(configuration, acquisitionId, path, extension));
        } catch (IOException e) {
            log.error("Cannot save Configuration to File for acquisitionId={} and configuration={}", acquisitionId, configuration);
            return Optional.empty();
        }
    }

    private void verifyInput(Long acquisitionId, JsonNode configuration) {
        Objects.requireNonNull(acquisitionId);
        Objects.requireNonNull(configuration);
    }

    protected File saveJsonNodeToFile(JsonNode jsonNode, Long acquisitionId, String path, String extension) throws IOException {

        File file = createAcquisitionFile(acquisitionId, path, extension);
        String string = new YAMLMapper().writeValueAsString(jsonNode);
        InputStream source = new ByteArrayInputStream(string.getBytes());
        FileUtils.copyInputStreamToFile(source, file);

        return file;
    }

    private File createAcquisitionFile(Long acquisitionId, String path, String extension) {
        String pathName = path + acquisitionId;
        File dir = new File(pathName);
        if (!dir.mkdirs()) {
            throw new ApplicationContextException("Cannot create dir: " + pathName);
        }
        String filename = path + acquisitionId + extension;
        return new File(dir, filename);
    }
}
