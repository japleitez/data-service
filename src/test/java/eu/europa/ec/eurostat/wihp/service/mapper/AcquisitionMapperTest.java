package eu.europa.ec.eurostat.wihp.service.mapper;

import static eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionStatusEnum.PROVISIONING;

import eu.europa.ec.eurostat.wihp.domain.Acquisition;
import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.service.dto.AcquisitionDTO;
import eu.europa.ec.eurostat.wihp.service.dto.ActionLogDTO;
import eu.europa.ec.eurostat.wihp.web.rest.CrawlerResourceIT;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

class AcquisitionMapperTest {


    @Test
    public void whenCreateAcquisitionDTO_thenMap() {
        UUID uuid = UUID.randomUUID();

        Crawler crawler = CrawlerResourceIT.createCrawlerEntity();
        Acquisition acquisition = new Acquisition(uuid, crawler);
        AcquisitionDTO dto = Mappers.getMapper( AcquisitionMapper.class ).toDto(acquisition);

        Assertions.assertEquals(acquisition.getLastUpdateDate(),dto.getLastUpdateDate());
        Assertions.assertEquals(acquisition.getStartDate(),dto.getStartDate());
        Assertions.assertEquals(acquisition.getStatus(), dto.getStatus());
        Assertions.assertEquals(acquisition.getCrawler().getName(), dto.getCrawlerName());
        Assertions.assertEquals(acquisition.getWorkflowId(), dto.getWorkflowId());
    }

}
