package eu.europa.ec.eurostat.wihp.service.mapper;

import org.springframework.stereotype.Component;

@Component
public class MappersContainer {

    private final CrawlerMapper crawlerMapper;
    private final SourceMapper sourceMapper;
    private final ParserFilterMapper parserFilterMapper;
    private final UrlFilterMapper urlFilterMapper;
    private final ParseFilterMapper parseFilterMapper;

    public MappersContainer(CrawlerMapper crawlerMapper,
                            SourceMapper sourceMapper,
                            ParserFilterMapper parserFilterMapper,
                            UrlFilterMapper urlFilterMapper,
                            ParseFilterMapper parseFilterMapper ) {

        this.crawlerMapper = crawlerMapper;
        this.sourceMapper = sourceMapper;
        this.parserFilterMapper = parserFilterMapper;
        this.urlFilterMapper = urlFilterMapper;
        this.parseFilterMapper = parseFilterMapper;
    }

    public CrawlerMapper getCrawlerMapper() {
        return crawlerMapper;
    }

    public SourceMapper getSourceMapper() {
        return sourceMapper;
    }

    public ParserFilterMapper getParserFilterMapper() {
        return parserFilterMapper;
    }

    public UrlFilterMapper getUrlFilterMapper() { return urlFilterMapper; }

    public ParseFilterMapper getParseFilterMapper() { return parseFilterMapper; }

}
