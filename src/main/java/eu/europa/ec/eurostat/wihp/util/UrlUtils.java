package eu.europa.ec.eurostat.wihp.util;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtils {

    public static boolean isValidUrl(String url) {
        if (url == null) {
            return false;
        }
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }
}
