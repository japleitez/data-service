package eu.europa.ec.eurostat.wihp.service.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.service.dto.CrawlerDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceListDTO;
import eu.europa.ec.eurostat.wihp.util.JsonModelUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

class MultiPartFileMapperTest {


    MultiPartFileMapper multiPartFileMapper = new MultiPartFileMapper(new ObjectMapper());

    @Test
    public void WhenCrawlerMultiPartFile_thenOptionalPresentTest() throws IOException {
        final int sourcesSize = 100;
        MockMultipartFile file = JsonModelUtils.getMockMultipartFileForCrawler("fileToUpload0",sourcesSize,false);
        CrawlerDTO c =  multiPartFileMapper.convert(file,CrawlerDTO.class);
        Assertions.assertNotNull(c);
        Assertions.assertFalse(c.getSources().isEmpty());
    }

    @Test
    public void WhenSourcesMultiPartFile_thenOptionalPresentTest() throws IOException {

        final int sourcesSize = 100;
        MockMultipartFile file = JsonModelUtils.getMockMultipartFileForSources("",sourcesSize, false, "fileToUpload0");
        SourceListDTO c =  multiPartFileMapper.convert(file, SourceListDTO.class);
        Assertions.assertNotNull(c);
        Assertions.assertFalse(c.getSources().isEmpty());
    }

}
