package eu.europa.ec.eurostat.wihp.web.rest.model;

import eu.europa.ec.eurostat.wihp.domain.Crawler;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CopyCrawlerRequest {

    @NotNull
    @Size(min = 1, max = 100)
    @Pattern(regexp = Crawler.NAME_REGEX)
    String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "CopyCrawlerRequest{" + "name='" + name + '\'' + '}';
    }
}
