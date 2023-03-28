package eu.europa.ec.eurostat.wihp.service.crawlers;

import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.service.dto.CrawlerDTO;
import eu.europa.ec.eurostat.wihp.service.mapper.CrawlerMapper;

public class CrawlerPartialUpdateUtility {

    private final CrawlerMapper crawlerMapper;

    public CrawlerPartialUpdateUtility(CrawlerMapper crawlerMapper) {
        this.crawlerMapper = crawlerMapper;
    }

    public Crawler partialUpdate(Crawler existingCrawler, CrawlerDTO crawlerDTO) {
        if (crawlerDTO.getName() != null) {
            existingCrawler.setName(crawlerDTO.getName());
        }
        if (crawlerDTO.getFetchInterval() != null) {
            existingCrawler.setFetchInterval(crawlerDTO.getFetchInterval());
        }
        if (crawlerDTO.getFetchIntervalWhenError() != null) {
            existingCrawler.setFetchIntervalWhenError(crawlerDTO.getFetchIntervalWhenError());
        }
        if (crawlerDTO.getFetchIntervalWhenFetchError() != null) {
            existingCrawler.setFetchIntervalWhenFetchError(crawlerDTO.getFetchIntervalWhenFetchError());
        }
        if (crawlerDTO.getExtractorNoText() != null) {
            existingCrawler.setExtractorNoText(crawlerDTO.getExtractorNoText());
        }
        if (crawlerDTO.getExtractorTextIncludePattern() != null) {
            existingCrawler.setExtractorTextIncludePattern(crawlerDTO.getExtractorTextIncludePattern());
        }
        if (crawlerDTO.getExtractorTextExcludeTags() != null) {
            existingCrawler.setExtractorTextExcludeTags(crawlerDTO.getExtractorTextExcludeTags());
        }
        if (crawlerDTO.getHttpContentLimit() != null) {
            existingCrawler.setHttpContentLimit(crawlerDTO.getHttpContentLimit());
        }
        if (crawlerDTO.getEmitOutLinks() != null) {
            existingCrawler.setEmitOutLinks(crawlerDTO.getEmitOutLinks());
        }
        if (crawlerDTO.getMaxEmitOutLinksPerPage() != null) {
            existingCrawler.setMaxEmitOutLinksPerPage(crawlerDTO.getMaxEmitOutLinksPerPage());
        }
        if (crawlerDTO.getDynamic() != null) {
            existingCrawler.setDynamic(crawlerDTO.getDynamic());
        }
        if (crawlerDTO.getDynamicConfig() != null) {
            existingCrawler.setDynamicConfig(crawlerMapper.map(crawlerDTO.getDynamicConfig()));
        }
        return existingCrawler;
    }
}
