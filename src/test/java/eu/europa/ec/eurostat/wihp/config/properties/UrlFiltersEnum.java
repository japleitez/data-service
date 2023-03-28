package eu.europa.ec.eurostat.wihp.config.properties;

public enum UrlFiltersEnum {
    FILTERING_BASIC_BASIC_URL_FILTER("com.digitalpebble.stormcrawler.filtering.basic.BasicURLFister"),
    FILTERING_DEPTH_MAX_DEPTH_FILTER("com.digitalpebble.stormcrawler.filtering.depth.MaxDepthFilter"),
    FILTERING_BASIC_BASIC_URL_NORMALIZER("com.digitalpebble.stormcrawler.filtering.basic.BasicURLNormalizer"),
    FILTERING_HOST_HOST_URL_FILTER("com.digitalpebble.stormcrawler.filtering.host.HostURLFilter"),
    FILTERING_HOST_REGEX_URL_NORMALIZER("com.digitalpebble.stormcrawler.filtering.regex.RegexURLNormalizer"),
    FILTERING_REGEX_REGEX_URL_FILTER("com.digitalpebble.stormcrawler.filtering.regex.RegexURLFilter"),
    FILTERING_BASIC_SELF_URL_FILTER("com.digitalpebble.stormcrawler.filtering.basic.SelfURLFilter"),
    FILTERING_SITEMAP_SITEMAP_FILTER("com.digitalpebble.stormcrawler.filtering.sitemap.SitemapFilter"),
    FILTERING_REGEX_FAST_URL_FILTER("eu.europa.ec.eurostat.wihp.filters.url.EsFastUrlFilter"),
    FILTERING_WIHP_URL_FILTER("eu.europa.ec.eurostat.wihp.filters.url.WIHPFilters");

    private final String classPath;

    UrlFiltersEnum(String classPath) {
        this.classPath = classPath;
    }

    public String getClassPath() {
        return classPath;
    }
}
