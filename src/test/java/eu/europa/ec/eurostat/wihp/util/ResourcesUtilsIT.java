package eu.europa.ec.eurostat.wihp.util;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContextException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ResourcesUtilsIT {

    ResourcesUtils resources = new ResourcesUtils();

    public static <T> String getStringFromResourceFile(T theType, String fileName) throws IOException, URISyntaxException {
        URI uri = Objects.requireNonNull(theType.getClass().getResource(fileName)).toURI();
        return IOUtils.toString(uri, StandardCharsets.UTF_8);
    }

    @Test
    public void fileFound() {
        File file = resources.getFile("./config/bootstrap.yml");
        assertNotNull(file);
        assertTrue(file.exists());
    }

    @Test
    public void fileNotFound_throwsIllegalArgumentException() {
        assertThrows(ApplicationContextException.class, () -> resources.getFile("not_found.txt"));
    }

    @Test
    public void readFile_test() {
        String fileContent = resources.readFileAsString("./config/bootstrap.yml");
        assertNotNull(fileContent);
        assertFalse(fileContent.isBlank());
        assertTrue(fileContent.contains("spring:"));
    }

    @Test
    public void readFile_testWrongName() {
        ApplicationContextException thrown =
            assertThrows(ApplicationContextException.class, () -> resources.readFileAsString("./config/wrongName.txt"));
        assertTrue(thrown.getMessage().contains("wrongName.txt"));
        assertEquals("Can not read from Resource file ./config/wrongName.txt", thrown.getMessage());
    }

}
