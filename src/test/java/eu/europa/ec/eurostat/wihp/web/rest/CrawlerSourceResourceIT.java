package eu.europa.ec.eurostat.wihp.web.rest;

import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.domain.Source;
import eu.europa.ec.eurostat.wihp.repository.CrawlerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class CrawlerSourceResourceIT {

    private static final String API_CRAWLER_PATH = "/api/crawlers/{crawlerId}/sources/";

    private static final String FULL_PATH = API_CRAWLER_PATH + "{sourceId}";

    @Autowired
    private CrawlerRepository crawlerRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCrawlerMockMvc;

    private Crawler crawler;

    @BeforeEach
    public void initTest() {
        crawler = CrawlerResourceIT.createEntity(em);
    }

    @Test
    @Transactional
    void getFirstPageCrawlerSources() throws Exception {
        // Initialize the database
        crawlerRepository.saveAndFlush(crawler);

        Long crawlerId = crawler.getId();
        List<Source> source = new ArrayList<>(crawler.getSources());

        restCrawlerMockMvc
            .perform(get( API_CRAWLER_PATH+ "?page=0&size=1", crawlerId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(source.get(0).getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(source.get(0).getName())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(source.get(0).getUrl())));
    }


    @Test
    @Transactional
    void postCrawlerSource() throws Exception {
        // Initialize the database
        crawlerRepository.saveAndFlush(crawler);

        Long sourceId = crawler.getSources()
            .stream()
            .findFirst()
            .get()
            .getId();
        Long crawlerId = crawler.getId();
        restCrawlerMockMvc
            .perform(post(FULL_PATH, crawlerId, sourceId))
            .andExpect(status().isNoContent());
    }


    @Test
    @Transactional
    void deleteCrawlerSource() throws Exception {
        // Initialize the database
        crawlerRepository.saveAndFlush(crawler);

        Long sourceId = crawler.getSources()
            .stream()
            .findFirst()
            .get()
            .getId();
        Long crawlerId = crawler.getId();
        restCrawlerMockMvc
            .perform(delete( FULL_PATH, crawlerId, sourceId))
            .andExpect(status().isNoContent());
    }


}
