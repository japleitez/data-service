package eu.europa.ec.eurostat.wihp.service.crawlers;


import eu.europa.ec.eurostat.wihp.IntegrationTest;
import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.domain.Source;
import eu.europa.ec.eurostat.wihp.repository.CrawlerRepository;
import eu.europa.ec.eurostat.wihp.repository.SourceRepository;
import eu.europa.ec.eurostat.wihp.service.dto.CrawlerDTO;
import eu.europa.ec.eurostat.wihp.service.mapper.CrawlerMapper;
import eu.europa.ec.eurostat.wihp.web.rest.CrawlerResourceIT;
import eu.europa.ec.eurostat.wihp.web.rest.SourceResourceIT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@IntegrationTest
public class CrawlerSourceServiceIT {

    private CrawlerSourceService unit;

    @Autowired
    private CrawlerMapper crawlerMapper;


    @Mock
    private CrawlerRepository mockCrawlerRepository;

    @Mock
    private SourceRepository mockSourceRepository;

    @BeforeEach
    public void setUp() {
        unit = new CrawlerSourceService(mockCrawlerRepository, crawlerMapper, mockSourceRepository);
    }

    @Test
    public void whenUnLinkSourceFromCrawler_andSourceIdValidThenSaveIsCalled() {

        Crawler crawler = CrawlerResourceIT.createCrawlerEntity();
        crawler.setId(233L);
        Source existing = SourceResourceIT.createEntity(null);
        existing.setId(131L);
        crawler.addSource(existing);
        Source newSource = SourceResourceIT.createEntity(null);
        newSource.setId(133L);
        crawler.addSource(newSource);

        when(mockCrawlerRepository.findById(crawler.getId())).thenReturn(Optional.of(crawler));
        when(mockSourceRepository.findSourceById(newSource.getId())).thenReturn(Optional.of(newSource));

        Crawler updated = crawlerMapper.copy(crawler,crawler.getName());
        updated.removeSource(newSource);

        when(mockCrawlerRepository.save(any(Crawler.class))).thenReturn(updated);


        CrawlerDTO dto = unit.removeSourceFromCrawler(crawler.getId(),newSource.getId());

        Assertions.assertEquals(1,dto.getSources().size());
        Assertions.assertFalse(dto.getSources().stream().anyMatch(s->s.getId().equals(newSource.getId())));

    }

    @Test
    public void whenLinkSourceFromCrawler_andSourceIdValidThenSaveIsCalled() {

        Crawler crawler = CrawlerResourceIT.createCrawlerEntity();
        crawler.setId(233L);
        Source existing = SourceResourceIT.createEntity(null);
        existing.setId(131L);
        crawler.addSource(existing);

        Source newSource = SourceResourceIT.createEntity(null);
        newSource.setId(133L);

        when(mockCrawlerRepository.findById(crawler.getId())).thenReturn(Optional.of(crawler));
        when(mockSourceRepository.findSourceById(newSource.getId())).thenReturn(Optional.of(newSource));

        Crawler updated = crawlerMapper.copy(crawler,crawler.getName());
        updated.addSource(newSource);
        when(mockCrawlerRepository.save(any(Crawler.class))).thenReturn(updated);

        CrawlerDTO dto = unit.addSourceToCrawler(crawler.getId(),newSource.getId());

        Assertions.assertEquals(2,dto.getSources().size());
        Assertions.assertTrue(dto.getSources().stream().anyMatch(s->s.getId().equals(newSource.getId())));
    }
}
