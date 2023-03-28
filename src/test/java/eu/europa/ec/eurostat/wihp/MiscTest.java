package eu.europa.ec.eurostat.wihp;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MiscTest {

    @Test
    public void checkUrlSource() throws MalformedURLException {
        String link = "https://ec.europa.eu/eurostat/data/database";
        URL url = new URL(link);
        System.out.println(url.getHost());
        Assertions.assertEquals("ec.europa.eu", url.getHost());
    }

    @Test
    public void checkDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = Date.from(Instant.now());
        Assertions.assertEquals(24, dateFormat.format(date).length());
        System.out.println(dateFormat.format(date).length());
    }
}
