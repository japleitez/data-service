package eu.europa.ec.eurostat.wihp.service.playground;

import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.security.oauth2.AuthorizationHeaderUtil;
import eu.europa.ec.eurostat.wihp.service.dto.ParseFilterDTO;
import eu.europa.ec.eurostat.wihp.service.dto.UrlFilterDTO;
import eu.europa.ec.eurostat.wihp.service.playground.model.ValidationFilterResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContextException;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlaygroundValidationServiceTest {

    private PlaygroundValidationService playgroundService;

    private AuthorizationHeaderUtil authorizationHeaderUtil;
    private RestTemplateBuilder restTemplateBuilder;
    private RestTemplate restTemplate;
    @BeforeEach
    public void setUp() {
        authorizationHeaderUtil = mock(AuthorizationHeaderUtil.class);
        restTemplateBuilder = mock(RestTemplateBuilder.class);
        restTemplate = mock(RestTemplate.class);

        when(restTemplateBuilder.setConnectTimeout(Duration.ofSeconds(200))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.setReadTimeout(Duration.ofSeconds(200))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        playgroundService = new PlaygroundValidationService(
            restTemplateBuilder,
            getAppProperties("playHost"),
            authorizationHeaderUtil
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"http://some.host.there:987", "http://some.host.there:987/", " http://some.host.there:987", "   http://some.host.there:987   ", "    http://some.host.there:987/   "})
    public void buildPlaygroundUrlFilterUrl_test(String hostTemplate) {
        assertEquals("http://some.host.there:987/api/url-filters/validate", playgroundService.buildPlaygroundUrlFilterUrl(getAppProperties(hostTemplate)));
    }

    @Test
    public void buildPlaygroundUrlFilterUrl_test_emptyHost() {
        ApplicationContextException thrown =
            assertThrows(ApplicationContextException.class, () -> playgroundService.buildPlaygroundUrlFilterUrl(getAppProperties(null)));
        assertTrue(thrown.getMessage().contains("Missed Playground host"));

        thrown = assertThrows(ApplicationContextException.class, () -> playgroundService.buildPlaygroundUrlFilterUrl(getAppProperties("")));
        assertTrue(thrown.getMessage().contains("Missed Playground host"));

        thrown = assertThrows(ApplicationContextException.class, () -> playgroundService.buildPlaygroundUrlFilterUrl(getAppProperties(" ")));
        assertTrue(thrown.getMessage().contains("Missed Playground host"));
    }

    @Test
    public void createCustomFilterValidationRequest_UrlFiltertest() {
        List<UrlFilterDTO> filterList = Arrays.asList(
            getUrlFilterDTO(11L, "filter11"),
            getUrlFilterDTO(12L, "filter12")
        );

        RequestEntity<String> request = playgroundService.createCustomFilterValidationRequest(filterList, "token_test", "testUrl");

        assertEquals("token_test", request.getHeaders().get("Authorization").get(0));
        assertTrue(((String) request.getBody()).contains("\"id\":\"filter11\""));
        assertTrue(((String) request.getBody()).contains("\"id\":\"filter12\""));
    }

    @Test
    public void createCustomFilterValidationRequest_ParseFiltertest() {
        List<ParseFilterDTO> parseFilterList = Arrays.asList(
            getParseFilterDTO(21L, "filterParse11"),
            getParseFilterDTO(22L, "filterParse12")
        );

        RequestEntity<String> request = playgroundService.createCustomFilterValidationRequest(parseFilterList, "token_test", "testUrl");

        assertEquals("token_test", request.getHeaders().get("Authorization").get(0));
        assertTrue(((String) request.getBody()).contains("\"id\":\"filterParse11\""));
        assertTrue(((String) request.getBody()).contains("\"id\":\"filterParse12\""));
    }

    @Test
    public void getToken_test() {
        when(authorizationHeaderUtil.getAuthorizationHeader()).thenReturn(Optional.of("returnToken"));
        assertEquals("returnToken", playgroundService.getToken());
    }

    @Test
    public void getToken_testEmptyToken() {
        when(authorizationHeaderUtil.getAuthorizationHeader()).thenReturn(Optional.empty());

        OAuth2AuthorizationException thrown = assertThrows(OAuth2AuthorizationException.class, () -> playgroundService.getToken());

        assertTrue(thrown.getMessage().contains("empty_token"));
        assertTrue(thrown.getMessage().contains("PlaygroundValidationService: The token is empty"));
    }

    @Test
    public void validateUrlFilters_test(){
        List<UrlFilterDTO> filterList = Arrays.asList(
            getUrlFilterDTO(11L, "filter11"),
            getUrlFilterDTO(12L, "filter12")
        );
        when(authorizationHeaderUtil.getAuthorizationHeader()).thenReturn(Optional.of("returnToken"));
        when(playgroundService.restTemplateValidation.exchange(any(RequestEntity.class), any(Class.class)))
            .thenReturn(ResponseEntity.of(Optional.of(createValidationResult(2,1))));

        ValidationFilterResult validationFilterResult = playgroundService.validateUrlFilters(filterList);
        assertEquals(2, validationFilterResult.getConfigurations().size());
    }

    @Test
    public void validateParseFilters_test(){
        List<ParseFilterDTO> filterList = Arrays.asList(
            getParseFilterDTO(11L, "filter11"),
            getParseFilterDTO(12L, "filter12")
        );
        when(authorizationHeaderUtil.getAuthorizationHeader()).thenReturn(Optional.of("returnToken"));
        when(playgroundService.restTemplateValidation.exchange(any(RequestEntity.class), any(Class.class)))
            .thenReturn(ResponseEntity.of(Optional.of(createValidationResult(3,1))));

        ValidationFilterResult validationFilterResult = playgroundService.validateParseFilters(filterList);
        assertEquals(3, validationFilterResult.getConfigurations().size());
    }

    private UrlFilterDTO getUrlFilterDTO(Long id, String filterName) {
        UrlFilterDTO filterDTO = new UrlFilterDTO();
        filterDTO.setId(id);
        filterDTO.setFilterId(filterName);
        return filterDTO;
    }

    private ParseFilterDTO getParseFilterDTO(Long id, String filterName) {
        ParseFilterDTO filterDTO = new ParseFilterDTO();
        filterDTO.setId(id);
        filterDTO.setFilterId(filterName);
        return filterDTO;
    }

    private ApplicationProperties getAppProperties(String hostName) {
        ApplicationProperties prop = new ApplicationProperties();
        prop.setPlaygroundServiceAddress(hostName);
        return prop;
    }


    private ValidationFilterResult createValidationResult(int configNumber, int errorNumber) {
        ValidationFilterResult val = new ValidationFilterResult();
        val.setConfigurations(createValidationConfigList(configNumber, errorNumber));
        return val;
    }

    private List<ValidationFilterResult.ValidationFilterResultConfiguration> createValidationConfigList(int configNumber, int errorNumber) {
        List<ValidationFilterResult.ValidationFilterResultConfiguration> list = new ArrayList<>();
        for (int i = 0; i < configNumber; i++) {
            list.add(createValidationConfiguration(errorNumber));
        }
        return list;
    }


    private ValidationFilterResult.ValidationFilterResultConfiguration createValidationConfiguration(int errorNumber) {
        ValidationFilterResult.ValidationFilterResultConfiguration validationConfiguration
            = new ValidationFilterResult.ValidationFilterResultConfiguration();
        validationConfiguration.setId("111");
        return validationConfiguration;
    }

}
