package eu.europa.ec.eurostat.wihp.service.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.faker.CrawlerFaker;
import eu.europa.ec.eurostat.wihp.faker.SourceFaker;
import eu.europa.ec.eurostat.wihp.service.bulk.BulkSourceRepository;
import eu.europa.ec.eurostat.wihp.service.dto.CrawlerDTO;
import eu.europa.ec.eurostat.wihp.service.dto.CrawlerResponseDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceDTO;
import eu.europa.ec.eurostat.wihp.util.ResourcesUtilsIT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

class CrawlerImportServiceValidationTest {

    private final static String CRAWLER_JSON_MODEL = "/crawler_response_dto.json";

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    @Mock
    private BulkSourceRepository bulkSourceRepository = Mockito.mock(BulkSourceRepository.class);


    CrawlerImportServiceValidation unit = new CrawlerImportServiceValidation(factory.getValidator(), new BulkServiceValidation(factory.getValidator(), bulkSourceRepository));

    @Test
    void shouldLoadTheModel() throws IOException, URISyntaxException {

        ObjectMapper mapper = new ObjectMapper();
        String json = ResourcesUtilsIT.getStringFromResourceFile(this, CRAWLER_JSON_MODEL );

        CrawlerResponseDTO parent = mapper.readValue(json, CrawlerResponseDTO.class);
        Assertions.assertNotNull(parent);
    }

    @Test
    void whenCrawlerModelCorrect_then_validationReturnEmptyLists() {

        CrawlerDTO crawlerDTO = CrawlerFaker.createFakeCrawlerDTO();
        crawlerDTO.addSources(SourceFaker.createFakeSourceDTOs(5));

        CrawlerResponseDTO response = unit.validateCrawler(crawlerDTO);

        Assertions.assertTrue(response.getFieldErrors().isEmpty());
        Assertions.assertFalse (response.getSourceErrors().stream().anyMatch (f->f.getErrors().size()!=0));

    }

    @Test
    void whenCrawlerFieldInvalid_then_validationReturnErrorList() {

        CrawlerDTO crawlerDTO = CrawlerFaker.createFakeCrawlerDTO();
        crawlerDTO.addSources(SourceFaker.createFakeSourceDTOs(5));

        crawlerDTO.setName("#-@");
        crawlerDTO.setFetchInterval(-5);
        crawlerDTO.setFetchIntervalWhenError(-5);

        List<SourceDTO> sources = SourceFaker.createFakeSourceDTOs(1);
        sources.add(new SourceDTO().setName("@#"));
        sources.add(new SourceDTO().setUrl("@#"));
        crawlerDTO.addSources(sources);

        CrawlerResponseDTO response = unit.validateCrawler(crawlerDTO);

        Assertions.assertEquals(3, response.getFieldErrors().size());
        Assertions.assertTrue (response.getFieldErrors().stream().anyMatch (f->f.field.equals("name")));
        Assertions.assertTrue (response.getFieldErrors().stream().anyMatch (f->f.field.equals("fetchInterval")));
        Assertions.assertTrue (response.getFieldErrors().stream().anyMatch (f->f.field.equals("fetchIntervalWhenError")));
    }
}
