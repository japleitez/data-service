package eu.europa.ec.eurostat.wihp.service.crawlers;

import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.domain.Source;
import eu.europa.ec.eurostat.wihp.repository.CrawlerRepository;
import eu.europa.ec.eurostat.wihp.repository.SourceRepository;
import eu.europa.ec.eurostat.wihp.service.dto.CrawlerDTO;
import eu.europa.ec.eurostat.wihp.service.mapper.CrawlerMapper;
import eu.europa.ec.eurostat.wihp.web.rest.errors.BadRequestAlertException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CrawlerSourceService {

    public static final String ENTITY_NAME = "dataAcquisitionServiceCrawlerSourceService";
    private final CrawlerRepository crawlerRepository;
    private final CrawlerMapper crawlerMapper;
    private final SourceRepository sourceRepository;

    private static final String ERROR_KEY_NOTFOUND = "idnotfound";

    public CrawlerSourceService(
        final CrawlerRepository crawlerRepository,
        final CrawlerMapper crawlerMapper,
        final SourceRepository sourceRepository
    ) {
        this.crawlerRepository = crawlerRepository;
        this.crawlerMapper = crawlerMapper;
        this.sourceRepository = sourceRepository;
    }

    public Page<Source> getCrawlerSourcesPaginated(final Long id, final Pageable pageable) {
        Crawler crawler = crawlerRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Crawler Entity not found", ENTITY_NAME, ERROR_KEY_NOTFOUND));

        return sourceRepository.findSourcesByCrawlers(crawler, pageable);
    }

    public CrawlerDTO addSourceToCrawler(final Long crawlerId, final Long sourceId) {
        Crawler crawler = validateCrawlerId(crawlerId);
        Source source = validateSourceId(sourceId);

        crawler.addSource(source);

        return crawlerMapper.toDto(crawlerRepository.save(crawler));
    }

    public CrawlerDTO removeSourceFromCrawler(final Long crawlerId, final Long sourceId) {
        Crawler crawler = validateCrawlerId(crawlerId);
        Source source = validateSourceId(sourceId);
        crawler.getSources().remove(source);

        return crawlerMapper.toDto(crawlerRepository.save(crawler));
    }

    private Source validateSourceId(final Long sourceId) {
        return sourceRepository
            .findSourceById(sourceId)
            .orElseThrow(() -> new BadRequestAlertException("Source Entity not found", ENTITY_NAME, ERROR_KEY_NOTFOUND));
    }

    private Crawler validateCrawlerId(final Long crawlerId) {
        return crawlerRepository
            .findById(crawlerId)
            .orElseThrow(() -> new BadRequestAlertException("Crawler Entity not found", ENTITY_NAME, ERROR_KEY_NOTFOUND));
    }
}
