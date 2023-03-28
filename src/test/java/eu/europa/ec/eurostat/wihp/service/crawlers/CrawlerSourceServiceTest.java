package eu.europa.ec.eurostat.wihp.service.crawlers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.domain.Source;
import eu.europa.ec.eurostat.wihp.repository.CrawlerRepository;
import eu.europa.ec.eurostat.wihp.repository.SourceRepository;
import eu.europa.ec.eurostat.wihp.service.dto.CrawlerDTO;
import eu.europa.ec.eurostat.wihp.service.mapper.CrawlerMapper;
import eu.europa.ec.eurostat.wihp.web.rest.errors.BadRequestAlertException;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CrawlerSourceServiceTest {

    private CrawlerSourceService unit;

    @Mock
    private CrawlerRepository mockCrawlerRepository;

    @Mock
    private CrawlerMapper mockCrawlerMapper;

    @Mock
    private SourceRepository mockSourceRepository;

    @BeforeEach
    public void setUp() {
        unit = new CrawlerSourceService(mockCrawlerRepository, mockCrawlerMapper, mockSourceRepository);
    }

    @Test
    public void whenCrawlerHasNoSources_thenThrow() {
        Assertions.assertThrows(BadRequestAlertException.class, () -> unit.getCrawlerSourcesPaginated(1L, any(Pageable.class)));
    }

    @Test
    public void whenLinkSourceFromCrawler_andSourceIdNotValidThenThrow() {
        when(mockCrawlerRepository.findById(1L)).thenReturn(Optional.of(new Crawler()));
        when(mockSourceRepository.findSourceById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(BadRequestAlertException.class, () -> unit.addSourceToCrawler(1L, 1L));
    }

    @Test
    public void whenLinkSourceFromCrawler_andCrawlerIdNotValidThenThrow() {
        when(mockCrawlerRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(BadRequestAlertException.class, () -> unit.addSourceToCrawler(1L, 1L));
    }

    @Test
    public void whenUnLinkSourceFromCrawler_andSourceIdNotValidThenThrow() {
        when(mockCrawlerRepository.findById(1L)).thenReturn(Optional.of(new Crawler()));
        when(mockSourceRepository.findSourceById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(BadRequestAlertException.class, () -> unit.removeSourceFromCrawler(1L, 1L));
    }

    @Test
    public void whenUnLinkSourceFromCrawler_andCrawlerIdNotValidThenThrow() {
        when(mockCrawlerRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(BadRequestAlertException.class, () -> unit.removeSourceFromCrawler(1L, 1L));
    }

    @Test
    public void whenLinkSourceFromCrawler_andSourceIdValidThenSaveIsCalled() {
        when(mockCrawlerRepository.findById(1L)).thenReturn(Optional.of(new Crawler()));
        when(mockSourceRepository.findSourceById(1L)).thenReturn(Optional.of(new Source()));
        when(mockCrawlerMapper.toDto(any(Crawler.class))).thenReturn(new CrawlerDTO());
        when(mockCrawlerRepository.save(any(Crawler.class))).thenReturn(new Crawler());

        unit.addSourceToCrawler(1L, 1L);

        verify(mockCrawlerMapper).toDto(any(Crawler.class));
        verify(mockCrawlerRepository).save(any(Crawler.class));
    }

    @Test
    public void whenUnLinkSourceFromCrawler_andSourceIdValidThenSaveIsCalled() {
        when(mockCrawlerRepository.findById(1L)).thenReturn(Optional.of(new Crawler()));
        when(mockSourceRepository.findSourceById(1L)).thenReturn(Optional.of(new Source()));
        when(mockCrawlerMapper.toDto(any(Crawler.class))).thenReturn(new CrawlerDTO());
        when(mockCrawlerRepository.save(any(Crawler.class))).thenReturn(new Crawler());

        unit.removeSourceFromCrawler(1L, 1L);

        verify(mockCrawlerMapper).toDto(any(Crawler.class));
        verify(mockCrawlerRepository).save(any(Crawler.class));
    }

    @Test
    public void whenCrawlerHasSources_thenReturnPageable() {
        Page<Source> page = Mockito.mock(Page.class);
        Crawler crawler = new Crawler();
        when(mockCrawlerRepository.findById(1L)).thenReturn(Optional.of(crawler));
        when(mockSourceRepository.findSourcesByCrawlers(crawler, PageRequest.of(0, 1))).thenReturn(page);

        Page p = unit.getCrawlerSourcesPaginated(1L, PageRequest.of(0, 1));
        Assertions.assertEquals(page, p);
        verify(mockSourceRepository).findSourcesByCrawlers(crawler, PageRequest.of(0, 1));
    }
}
