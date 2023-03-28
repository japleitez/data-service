package eu.europa.ec.eurostat.wihp.util;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import eu.europa.ec.eurostat.wihp.faker.CrawlerFaker;
import eu.europa.ec.eurostat.wihp.faker.SourceFaker;
import eu.europa.ec.eurostat.wihp.service.dto.CrawlerDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceListDTO;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JsonModelUtils {

    public static MockMultipartFile getMockMultipartFileForCrawler(String name, int number, boolean save) throws IOException {
        String originalName = "crawler.json";
        File inFile = File.createTempFile("crawler", ".json");
        CrawlerDTO crawlerDTO = CrawlerFaker.createFakeCrawlerDTO();
        crawlerDTO.addSources(SourceFaker.createFakeSourceDTOs(number));
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(inFile, crawlerDTO);
        return createMockMultipartFile(inFile, name, originalName, save);
    }

    public static MockMultipartFile getMockMultipartFileForSources(String special, int n, boolean save, String name) throws IOException {
        String originalName = "sources.json";
        File inFile = File.createTempFile("sources", ".json");
        SourceListDTO model = createLongListOfSources(n, special);
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(inFile, model);
        return createMockMultipartFile(inFile, name, originalName, save);
    }

    private static MockMultipartFile createMockMultipartFile(File file, String name, String originalName, boolean save) throws IOException {
        byte[] content = null;
        try {
            content = Files.readAllBytes(file.toPath());
        } catch (final IOException e) {
        }
        finally {
            file.deleteOnExit();
        }
        if (save){
            savetoResources(new String(content));
        }
        return new MockMultipartFile(
            name,
            originalName,
            MediaType.TEXT_PLAIN_VALUE,
            content
        );
    }

    public static void savetoResources(String content) throws IOException {
        String filename = "src/test/resources/test"
            .concat( Long.toString(new Timestamp(System.currentTimeMillis()).getTime()))
            .concat(".json");
        OutputStream os = new FileOutputStream( Files.createFile(Paths.get(filename)).toFile());
        final PrintStream printStream = new PrintStream(os);
        printStream.println(content);
        printStream.close();

    }


    public static byte[] getBytes(SourceListDTO model) throws IOException {
        File inFile = File.createTempFile("sources", ".json");
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(inFile, model);

        byte[] content = null;
        try {
            content = Files.readAllBytes(inFile.toPath());
        } catch (final IOException e) {
        } finally {
            inFile.deleteOnExit();
        }

        return content;
    }


    public static SourceListDTO createLongListOfSources(long N, String special) throws IOException {

        SourceListDTO model = new SourceListDTO();

        List<SourceDTO> cleanList = new ArrayList<SourceDTO>();

        for (int i =1;i <= N ; i++){
            String nameSteam =  "source".concat(special);
            String urlSteam =  "https://source";

            String randPath = UUID.randomUUID().toString().replace("-","");
            nameSteam.concat(randPath);
            SourceDTO child = new SourceDTO();
            child.setName(nameSteam.concat(randPath));
            child.setUrl(urlSteam.concat(randPath).concat(".com"));
            cleanList.add(child);
        }
        model.setSources(cleanList);

        return model;
    }

    public SourceListDTO getSourceListJsonModel(String name) throws java.io.IOException, java.net.URISyntaxException {
        String json_source = ResourcesUtilsIT.getStringFromResourceFile(this, name );
        return new ObjectMapper().readValue(json_source, SourceListDTO.class);
    }

    public SourceListDTO getSourceListFromString(String json_source) throws java.io.IOException, java.net.URISyntaxException {
        return new ObjectMapper().readValue(json_source, SourceListDTO.class);
    }


    public static List<String> getSingleViolationMessages(SourceListDTO sources) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        return sources.getSources().stream()
            .map(v->validator.validate(v))
            .flatMap(Collection::stream)
            .collect(Collectors.toList())
            .stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toList());
    }


    public static List<String> getGroupViolationMessages(SourceListDTO sources) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return new ArrayList<>(validator.validate(sources))
            .stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toList());

    }


}
