package eu.europa.ec.eurostat.wihp.storm.elastic;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.net.InternetDomainName;
import org.springframework.context.ApplicationContextException;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class StormSource {

    private static final String STORM_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private final SimpleDateFormat format = new SimpleDateFormat(STORM_DATE_FORMAT, Locale.US);

    private final String url;
    private final String status;
    private final Metadata metadata;
    private final String key;
    private final String nextFetchDate;

    public StormSource(final String url) {
        this.url = url;
        this.metadata = new Metadata();
        this.status = "DISCOVERED";
        this.key = getKey(url);
        this.nextFetchDate = nextFetchDate();
    }

    private String getKey(String url) {
        try {
            return InternetDomainName.from(new URL(url).getHost()).topPrivateDomain().toString();
        } catch (MalformedURLException e) {
            throw new ApplicationContextException("Unable to create StormSource url form " + url, e);
        }
    }

    private String nextFetchDate() {
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(Date.from(Instant.now()));
    }

    public String getUrl() {
        return url;
    }

    public String getStatus() {
        return status;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public String getKey() {
        return key;
    }

    public String getNextFetchDate() {
        return nextFetchDate;
    }

    @JsonSerialize
    public static class Metadata {
    }
}
