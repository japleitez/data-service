package eu.europa.ec.eurostat.wihp.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DynamicConfigDTO implements Serializable {

    private static final String LANGUAGE_PATTERN = "[a-zA-Z]{2}[-]?[a-zA-Z]{0,2}";

    private static final String WINDOW_SIZE_PATTERN = "[0-9]{2,4}[,]+[0-9]{2,4}";

    @Size(min = 2, max = 5, message = "language length is not correct")
    @Pattern(regexp = LANGUAGE_PATTERN, message = "invalid language name")
    @NotNull(message = "language cannot be null")
    @JsonProperty("language")
    private String language = "de";

    @JsonProperty("maximized")
    private boolean startMaximized = true;

    @Pattern(regexp = WINDOW_SIZE_PATTERN, message = "invalid window-size")
    @NotNull(message = "window-size cannot be null")
    @JsonProperty("windowSize")
    private String windowSize = "1920,1080";

    @JsonProperty("loadImages")
    private SeleniumOptionsEnum loadImages = SeleniumOptionsEnum.ALLOW;

    @JsonProperty("allowCookies")
    private SeleniumOptionsEnum allowCookies = SeleniumOptionsEnum.BLOCK;

    @JsonProperty("allowGeolocation")
    private SeleniumOptionsEnum allowGeolocation = SeleniumOptionsEnum.BLOCK;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isStartMaximized() {
        return startMaximized;
    }

    public void setStartMaximized(boolean startMaximized) {
        this.startMaximized = startMaximized;
    }

    public String getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(String windowSize) {
        this.windowSize = windowSize;
    }

    public SeleniumOptionsEnum getLoadImages() {
        return loadImages;
    }

    public void setLoadImages(SeleniumOptionsEnum loadImages) {
        this.loadImages = loadImages;
    }

    public SeleniumOptionsEnum getAllowCookies() {
        return allowCookies;
    }

    public void setAllowCookies(SeleniumOptionsEnum allowCookies) {
        this.allowCookies = allowCookies;
    }

    public SeleniumOptionsEnum getAllowGeolocation() {
        return allowGeolocation;
    }

    public void setAllowGeolocation(SeleniumOptionsEnum allowGeolocation) {
        this.allowGeolocation = allowGeolocation;
    }
}
