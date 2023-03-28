package eu.europa.ec.eurostat.wihp.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static eu.europa.ec.eurostat.wihp.util.UrlUtils.isValidUrl;

public class UrlUtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "http://google.com",
        "https://google.com",
        "http://www.google.com:80",
        "https://www.google.com:80",
        "http://some.host.there:987/",
        " http://some.host.there:987",
        "   http://some.host.there:987   ",
        "    http://some.host.there:987/asd/aef/asfd/asf/dadf   ",
        "    http://some.host.there:987/   "})
    public void positiveTestsUrl(String urlToTest) {
        Assertions.assertTrue(isValidUrl(urlToTest));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "google.com",
        "htt://google.com:80/",
        " ttps://google.com:80",
        "http://some.host.there:80:80/asd/sad/sa "})
    public void negativeTestsUrl(String urlToTest) {
        Assertions.assertFalse(isValidUrl(urlToTest));
    }
}
