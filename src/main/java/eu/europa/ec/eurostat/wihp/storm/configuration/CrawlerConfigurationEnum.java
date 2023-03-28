package eu.europa.ec.eurostat.wihp.storm.configuration;

import static java.lang.Boolean.TRUE;

import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.util.WordUtils;

public enum CrawlerConfigurationEnum {
    FETCHINTERVAL_DEFAULT("config.fetchInterval.default") {
        @Override
        public Object getValue(Crawler crawler) {
            return crawler.getFetchInterval();
        }
    },
    FETCHINTERVAL_FETCH_ERROR("config.fetchInterval.fetch.error") {
        @Override
        public Object getValue(Crawler crawler) {
            return crawler.getFetchIntervalWhenFetchError();
        }
    },
    FETCHINTERVAL_ERROR("config.fetchInterval.error") {
        @Override
        public Object getValue(Crawler crawler) {
            return crawler.getFetchIntervalWhenError();
        }
    },
    TEXTEXTRACTOR_NO_TEXT("config.textextractor.no.text") {
        @Override
        public Object getValue(Crawler crawler) {
            return crawler.getExtractorNoText();
        }
    },
    TEXTEXTRACTOR_INCLUDE_PATTERN("config.textextractor.include.pattern") {
        @Override
        public Object getValue(Crawler crawler) {
            String value = crawler.getExtractorTextIncludePattern();
            return WordUtils.toListOfStrings(value);
        }
    },
    TEXTEXTRACTOR_EXCLUDE_TAGS("config.textextractor.exclude.tags") {
        @Override
        public Object getValue(Crawler crawler) {
            String value = crawler.getExtractorTextExcludeTags();
            return WordUtils.toListOfStrings(value);
        }
    },
    HTTP_CONTENT_LIMIT("config.http.content.limit") {
        @Override
        public Object getValue(Crawler crawler) {
            return crawler.getHttpContentLimit();
        }
    },
    HTTP_PROTOCOL_IMPLEMENTATION("config.http.protocol.implementation") {
        @Override
        public Object getValue(Crawler crawler) {
            if (TRUE.equals(crawler.getDynamic())) {
                return "com.digitalpebble.stormcrawler.protocol.selenium.RemoteDriverProtocol";
            } else {
                return "com.digitalpebble.stormcrawler.protocol.okhttp.HttpProtocol";
            }
        }
    },
    HTTPS_PROTOCOL_IMPLEMENTATION("config.https.protocol.implementation") {
        @Override
        public Object getValue(Crawler crawler) {
            if (TRUE.equals(crawler.getDynamic())) {
                return "com.digitalpebble.stormcrawler.protocol.selenium.RemoteDriverProtocol";
            } else {
                return "com.digitalpebble.stormcrawler.protocol.okhttp.HttpProtocol";
            }
        }
    };

    public final String propertyPath;

    CrawlerConfigurationEnum(String propertyPath) {
        this.propertyPath = propertyPath;
    }

    public abstract Object getValue(final Crawler crawler);
}
