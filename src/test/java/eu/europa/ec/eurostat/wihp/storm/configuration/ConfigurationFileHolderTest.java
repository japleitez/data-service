package eu.europa.ec.eurostat.wihp.storm.configuration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.util.JsonNodeUtils;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ConfigurationFileHolderTest {

    public static final String PATH = ".\\acquisition_";
    public static final String EXT = ".flux";
    private static Long DEFAULT_ACQUISITION_ID = 100L;
    private static String DEFAULT_JSON_NODE_STRING = "{ \"name\": \"value\" }";
    private static File DEFAULT_DIR = new File(ConfigurationFileHolderTest.PATH + DEFAULT_ACQUISITION_ID);
    private static File TEST_DIR = new File(ConfigurationFileHolderTest.PATH + (DEFAULT_ACQUISITION_ID + 1));
    private static JsonNode DEFAULT_JSON_NODE_CONFIG = JsonNodeUtils.createJsonNode(DEFAULT_JSON_NODE_STRING).get();
    private static String DEFAULT_FILE_NAME = ConfigurationFileHolderTest.PATH + DEFAULT_ACQUISITION_ID + ConfigurationFileHolderTest.EXT;


    ConfigurationFileHolder service = new ConfigurationFileHolder();

    @BeforeAll
    public static void startUp() throws IOException {
        cleanDirs();
    }

    @AfterAll
    public static void cleanUp() throws IOException {
        cleanDirs();
    }

    @Test
    public void whenSavingConfiguration_returnsFile() {
        Optional<File> result = service.save(DEFAULT_ACQUISITION_ID, DEFAULT_JSON_NODE_CONFIG, PATH, EXT );
        assertTrue(result.isPresent());
        assertTrue(result.get().getAbsolutePath().contains(DEFAULT_FILE_NAME));
    }

    @Test
    public void whenSavingWithNullAcquisitionId_throwsException() {
        Assertions.assertThrows(NullPointerException.class, () -> service.save(null, DEFAULT_JSON_NODE_CONFIG, PATH, EXT));
    }

    @Test
    public void whenSavingWithNullConfiguration_throwsException() {
        Assertions.assertThrows(NullPointerException.class, () -> service.save(DEFAULT_ACQUISITION_ID, null, PATH, EXT));
    }

    @Test
    public void whenThrowFileException_returnsOptionalEmpty() throws IOException {
        ConfigurationFileHolder spied = spy(ConfigurationFileHolder.class);
        doThrow(new IOException("Error occurred")).when(spied).saveJsonNodeToFile(any(), any(),any(), any());

        Optional<File> result = spied.save(DEFAULT_ACQUISITION_ID + 1, DEFAULT_JSON_NODE_CONFIG, PATH, EXT);

        assertTrue(result.isEmpty());
    }

    private static void cleanDirs() throws IOException {
        FileUtils.deleteDirectory(DEFAULT_DIR);
        FileUtils.deleteDirectory(TEST_DIR);
    }
}
