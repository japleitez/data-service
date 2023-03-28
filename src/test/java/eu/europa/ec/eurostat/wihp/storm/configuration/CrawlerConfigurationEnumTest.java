package eu.europa.ec.eurostat.wihp.storm.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.storm.configuration.CrawlerConfigurationEnum;
import eu.europa.ec.eurostat.wihp.web.rest.CrawlerResourceIT;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class CrawlerConfigurationEnumTest {


    @Test
    public void whenPropertyExist_thenReturnProperty() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Crawler crawlerEntity = CrawlerResourceIT.createCrawlerEntity();

        Assert.assertEquals(CrawlerResourceIT.DEFAULT_FETCH_INTERVAL , CrawlerConfigurationEnum.FETCHINTERVAL_DEFAULT.getValue(crawlerEntity));
    }
}
