package eu.europa.ec.eurostat.wihp.storm.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class YmlService {

    private static final Logger log = LoggerFactory.getLogger(YmlService.class);

    public Optional<JsonNode> read(final String name) {
        return isFilePresent(name).map(this::getJsonNode);
    }

    protected Optional<URL> isFilePresent(final String name) {
        return Optional.ofNullable(getClass().getClassLoader().getResource(name));
    }

    private JsonNode getJsonNode(final URL url) {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            File content = new File(url.getFile());
            return mapper.readTree(content);
        } catch (IOException e) {
            log.error("Error while loading yml file", e);
            return null;
        }
    }
}
