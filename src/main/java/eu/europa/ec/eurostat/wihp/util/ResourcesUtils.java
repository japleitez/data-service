package eu.europa.ec.eurostat.wihp.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextException;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ResourcesUtils {

    private static final Logger log = LoggerFactory.getLogger(ResourcesUtils.class);

    public File getFile(String filename) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(filename);
        if (resource == null) {
            throw new ApplicationContextException("file not found! " + filename);
        } else {
            try {
                return new File(resource.toURI());
            } catch (URISyntaxException e) {
                log.error("Error when instantiate resource file {}", filename);
                throw new ApplicationContextException("Error for getting file " + filename);
            }
        }
    }

    public String readFileAsString(String filename) {
        try {
            return FileUtils.readFileToString(getFile(filename), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Can not read from Resource file {}", filename);
            throw new ApplicationContextException("Can not read from Resource file " + filename);
        }
    }
}
