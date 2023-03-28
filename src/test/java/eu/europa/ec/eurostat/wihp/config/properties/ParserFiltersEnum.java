package eu.europa.ec.eurostat.wihp.config.properties;

public enum ParserFiltersEnum {
    PARSE_FILTER_COLLECTION_TAGGER("eu.europa.ec.eurostat.wihp.filters.parse.EsCollectionTagger"),
    PARSE_FILTER_XPATH_FILTER("com.digitalpebble.stormcrawler.parse.filter.XPathFilter"),
    PARSE_FILTER_LINK_PARSE_FILTER("com.digitalpebble.stormcrawler.parse.filter.LinkParseFilter"),
    PARSE_FILTER_DOMAIN_PARSE__FILTER("com.digitalpebble.stormcrawler.parse.filter.DomainParseFilter"),
    PARSE_FILTER_MIME_TYPE_NORMALIZATION("com.digitalpebble.stormcrawler.parse.filter.MimeTypeNormalization"),
    PARSE_FILTER_COMMA_SEPARATED_TO_MULTIVALUE_METADATA("com.digitalpebble.stormcrawler.parse.filter.CommaSeparatedToMultivaluedMetadata"),
    PARSE_FILTER_LD_JSON_PARSE_FILTER("com.digitalpebble.stormcrawler.parse.filter.LDJsonParseFilter"),
    PARSE_FILTER_MD5_SIGNATURE_PARSE_FILTER("com.digitalpebble.stormcrawler.parse.filter.MD5SignatureParseFilter"),
    PARSE_FILTER_WIHP_PARSE_FILTER("eu.europa.ec.eurostat.wihp.filters.parse.WIHPParseFilters");

    private final String classPath;

    ParserFiltersEnum(String classPath) {
        this.classPath = classPath;
    }

    public String getClassPath() {
        return classPath;
    }
}
