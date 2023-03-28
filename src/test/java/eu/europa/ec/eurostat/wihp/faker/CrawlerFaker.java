package eu.europa.ec.eurostat.wihp.faker;

import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.service.dto.CrawlerDTO;

import java.util.Random;

public class CrawlerFaker {

    public static final Integer FETCH_INTERVAL = 1440;

    private static final Integer FETCH_INTERVAL_WHEN_ERROR = 44640;

    private static final Integer FETCH_INTERVAL_WHEN_FETCH_ERROR = 1440;

    private static final Boolean EXTRACTOR_NO_TEXT = false;

    private static final String EXTRACTOR_TEXT_INCLUDE_PATTERN = "";

    private static final String EXTRACTOR_TEXT_EXCLUDE_TAGS = "";

    private static final Integer HTTP_CONTENT_LIMIT = 1440;

    private static final Boolean EMIT_OUT_LINKS = false;

    private static final Boolean DYNAMIC = false;

    private static final Integer MAX_EMIT_OUT_LINKS_PER_PAGE = -1;

    private static int NAME_LENGTH = 10;

    private static Random random = new Random();

    public static CrawlerDTO createFakeCrawlerDTO() {
        CrawlerDTO dto =  new CrawlerDTO();
        dto.setName(NameFaker.generateAlphabeticString(NAME_LENGTH));
        dto.setFetchInterval(FETCH_INTERVAL);
        dto.setFetchIntervalWhenError(FETCH_INTERVAL_WHEN_ERROR);
        dto.setFetchIntervalWhenFetchError(FETCH_INTERVAL_WHEN_FETCH_ERROR);
        dto.setExtractorNoText(EXTRACTOR_NO_TEXT);
        dto.setExtractorTextIncludePattern(EXTRACTOR_TEXT_INCLUDE_PATTERN);
        dto.setExtractorTextExcludeTags(EXTRACTOR_TEXT_EXCLUDE_TAGS);
        dto.setHttpContentLimit(HTTP_CONTENT_LIMIT);
        dto.setEmitOutLinks(EMIT_OUT_LINKS);
        dto.setDynamic(DYNAMIC);
        dto.setMaxEmitOutLinksPerPage(MAX_EMIT_OUT_LINKS_PER_PAGE);
        return dto;
    }

}
