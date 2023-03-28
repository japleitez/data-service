package eu.europa.ec.eurostat.wihp.faker;

public class UrlFaker {

    private static String HTTP_PREFIX = "https://";
    private static String DOT_COM = ".com";

    public static String generateUrl() {
        return HTTP_PREFIX + NameFaker.generateAlphabeticString(10) + DOT_COM;
    }

}
