package eu.europa.ec.eurostat.wihp.service.playground;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.security.oauth2.AuthorizationHeaderUtil;
import eu.europa.ec.eurostat.wihp.service.dto.ParseFilterDTO;
import eu.europa.ec.eurostat.wihp.service.dto.UrlFilterDTO;
import eu.europa.ec.eurostat.wihp.service.playground.model.ValidationFilterResult;
import eu.europa.ec.eurostat.wihp.util.JsonNodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContextException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.net.URI;
import java.time.Duration;
import java.util.List;

@Service
public class PlaygroundValidationService {

    private static final String VALIDATION_URL_FILTER_ENDPOINT = "api/url-filters/validate";
    private static final String VALIDATION_PARSE_FILTER_ENDPOINT = "api/parse-filters/validate";

    private static final Logger log = LoggerFactory.getLogger(PlaygroundValidationService.class);
    protected final RestTemplate restTemplateValidation;
    private final String playgroundFilterUrlValidationUrl;
    private final String playgroundParseUrlValidationUrl;
    private final AuthorizationHeaderUtil authorizationHeaderUtil;

    private final OAuth2Error tokenEmptyError = new OAuth2Error("empty_token", "PlaygroundValidationService: The token is empty", null);

    public PlaygroundValidationService(RestTemplateBuilder restTemplateBuilder,
                                       ApplicationProperties applicationProperties,
                                       AuthorizationHeaderUtil authorizationHeaderUtil) {

        this.authorizationHeaderUtil = authorizationHeaderUtil;
        this.playgroundFilterUrlValidationUrl = buildPlaygroundUrlFilterUrl(applicationProperties);
        this.playgroundParseUrlValidationUrl = buildValidationParseFilterUrl(applicationProperties);
        this.restTemplateValidation = restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(200))
            .setReadTimeout(Duration.ofSeconds(200))
            .build();
    }

    public ValidationFilterResult validateUrlFilters(List<UrlFilterDTO> filters) {
        ResponseEntity<ValidationFilterResult> responseEntity = this.restTemplateValidation
            .exchange(createCustomFilterValidationRequest(filters, getToken(), playgroundFilterUrlValidationUrl), ValidationFilterResult.class);
        return responseEntity.getBody();
    }

    public ValidationFilterResult validateParseFilters(List<ParseFilterDTO> filters) {
        ResponseEntity<ValidationFilterResult> responseEntity = this.restTemplateValidation
            .exchange(createCustomFilterValidationRequest(filters, getToken(), playgroundParseUrlValidationUrl), ValidationFilterResult.class);
        return responseEntity.getBody();
    }

    protected String getToken() {
        return authorizationHeaderUtil.getAuthorizationHeader().orElseThrow(() -> {
                log.error("PlaygroundValidationService : The token is empty");
                return new OAuth2AuthorizationException(tokenEmptyError);
            }
        );
    }

    protected RequestEntity<String> createCustomFilterValidationRequest(List<? extends Serializable> filters, String token, String urlString) {
        return RequestEntity.post(URI.create(urlString))
            .contentType(MediaType.APPLICATION_JSON)
            .headers(getAuthorizedHeaders(token))
            .body(JsonNodeUtils.createJsonString(new FiltersWrapper<>(filters)));
    }

    private HttpHeaders getAuthorizedHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    protected String buildPlaygroundUrlFilterUrl(ApplicationProperties applicationProperties) {
        String playgroundServiceAddress = applicationProperties.getPlaygroundServiceAddress();
        if (null == playgroundServiceAddress || playgroundServiceAddress.trim().length() == 0) {
            throw new ApplicationContextException("You have misconfigure your application! Missed Playground host configuration");
        }
        return ((!playgroundServiceAddress.trim().endsWith("/")) ? playgroundServiceAddress.trim() + "/" : playgroundServiceAddress.trim()) + VALIDATION_URL_FILTER_ENDPOINT;
    }

    protected String buildValidationParseFilterUrl(ApplicationProperties applicationProperties) {
        String playgroundServiceAddress = applicationProperties.getPlaygroundServiceAddress();
        if (null == playgroundServiceAddress || playgroundServiceAddress.trim().length() == 0) {
            throw new ApplicationContextException("You have misconfigure your application! Missed Playground host configuration");
        }
        return ((!playgroundServiceAddress.trim().endsWith("/")) ? playgroundServiceAddress.trim() + "/" : playgroundServiceAddress.trim()) + VALIDATION_PARSE_FILTER_ENDPOINT;
    }

    private static class FiltersWrapper<T> {
        @JsonProperty(value = "configurations")
        private List<T> configuration;

        public FiltersWrapper(List<T> configuration) {
            this.configuration = configuration;
        }

        public List<T> getConfiguration() {
            return configuration;
        }

        public void setConfiguration(List<T> configuration) {
            this.configuration = configuration;
        }
    }
}
