package eu.europa.ec.eurostat.wihp.service.crawlers;

import static java.util.stream.Collectors.groupingBy;
import static org.springframework.util.CollectionUtils.isEmpty;

import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.domain.ParseFilter;
import eu.europa.ec.eurostat.wihp.domain.ParserFilter;
import eu.europa.ec.eurostat.wihp.domain.UrlFilter;
import eu.europa.ec.eurostat.wihp.repository.CrawlerRepository;
import eu.europa.ec.eurostat.wihp.service.bulk.BulkSourcesService;
import eu.europa.ec.eurostat.wihp.service.dto.*;
import eu.europa.ec.eurostat.wihp.service.mapper.*;
import eu.europa.ec.eurostat.wihp.service.playground.PlaygroundValidationService;
import eu.europa.ec.eurostat.wihp.service.playground.model.ValidationFilterResult;
import eu.europa.ec.eurostat.wihp.service.validation.CrawlerImportServiceValidation;
import eu.europa.ec.eurostat.wihp.service.validation.ValidationResult;
import eu.europa.ec.eurostat.wihp.web.rest.errors.BadRequestAlertException;
import eu.europa.ec.eurostat.wihp.web.rest.errors.UnprocessableEntityException;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CrawlerService {

    public static final String ENTITY_NAME = "UpdateCrawlerService";
    public static final String PARSER_FILTERS = "ParserFilters";
    public static final String PARSER_FILTERS_DEPRECATED = "ParserFilters are deprecated";

    private final CrawlerRepository crawlerRepository;
    private final BulkSourcesService bulkSourcesService;
    private final CrawlerMapper crawlerMapper;
    private final CrawlerImportServiceValidation crawlerImportServiceValidation;
    private final SourceMapper sourceMapper;
    private final ParserFilterMapper parserFilterMapper;
    private final PlaygroundValidationService playgroundValidationService;
    private final UrlFilterMapper urlFilterMapper;
    private final ParseFilterMapper parseFilterMapper;
    private final CrawlerPartialUpdateUtility crawlerPartialUpdateUtility;

    private final Logger log = LoggerFactory.getLogger(CrawlerService.class);

    public CrawlerService(
        final CrawlerRepository crawlerRepository,
        final BulkSourcesService bulkSourcesService,
        final PlaygroundValidationService playgroundValidationService,
        final CrawlerImportServiceValidation crawlerImportServiceValidation,
        final MappersContainer mappersContainer
    ) {
        this.crawlerRepository = crawlerRepository;
        this.bulkSourcesService = bulkSourcesService;
        this.crawlerMapper = mappersContainer.getCrawlerMapper();
        this.crawlerImportServiceValidation = crawlerImportServiceValidation;
        this.sourceMapper = mappersContainer.getSourceMapper();
        this.playgroundValidationService = playgroundValidationService;
        this.parserFilterMapper = mappersContainer.getParserFilterMapper();
        this.urlFilterMapper = mappersContainer.getUrlFilterMapper();
        this.parseFilterMapper = mappersContainer.getParseFilterMapper();
        this.crawlerPartialUpdateUtility = new CrawlerPartialUpdateUtility(crawlerMapper);
    }

    public CrawlerDTO persistRequest(CrawlerDTO crawlerDTO) {
        ValidationResult<SourceResponseDTO> redundant = splitExistingSources(validateCrawler(crawlerDTO));
        List<SourceDTO> savedSources = bulkSourcesService.saveSources(redundant.getValidList());
        List<SourceDTO> existingSources = sourceMapper.fromResponseDtoListToSourceDtoList(redundant.getInvalidList());
        savedSources.addAll(existingSources);
        return saveCrawler(crawlerDTO, savedSources);
    }

    public CrawlerDTO createCrawler(final CrawlerDTO crawlerDTO) {
        validateCustomFilters(crawlerDTO);
        if (!isEmpty(crawlerDTO.getParserFilters())) {
            throw new UnprocessableEntityException(PARSER_FILTERS_DEPRECATED, ENTITY_NAME, PARSER_FILTERS);
        }
        return crawlerMapper.toDto(crawlerRepository.save(crawlerMapper.toEntity(crawlerDTO)));
    }

    public Optional<CrawlerDTO> copyCrawler(final Long id, String name) {
        return crawlerRepository
            .findById(id)
            .map(crawler -> crawlerMapper.copy(crawler, name))
            .map(crawlerRepository::save)
            .map(crawlerMapper::toDto);
    }

    public CrawlerDTO updateCrawler(CrawlerDTO crawlerDTO) {
        validateCustomFilters(crawlerDTO);
        return crawlerRepository
            .findById(crawlerDTO.getId())
            .map(crawler -> updateCrawler(crawlerDTO, crawler))
            .map(crawlerMapper::toDto)
            .orElseThrow(() -> new RuntimeException("Error while updating crawler"));
    }

    public List<SourceResponseDTO> validateCrawler(CrawlerDTO crawlerDTO) {
        ValidationResult<SourceResponseDTO> violations = validateSources(crawlerDTO);
        CrawlerResponseDTO response = crawlerImportServiceValidation.validateCrawler(crawlerDTO);
        response.setSourceErrors(violations.getInvalidList());
        if (hasAnyViolation(response)) {
            throw new UnprocessableEntityException("/api/crawlers/import", "Method argument not valid", "validation", response);
        }
        return violations.getValidList();
    }

    public ValidationResult<SourceResponseDTO> validateSources(final CrawlerDTO crawlerDTO) {
        return crawlerImportServiceValidation.validateSources(crawlerDTO);
    }

    public ValidationResult<SourceResponseDTO> splitExistingSources(List<SourceResponseDTO> validLIst) {
        return crawlerImportServiceValidation.splitRedundant(validLIst);
    }

    public Page<CrawlerDTO> getPageableCrawlerList(Pageable pageable) {
        return crawlerRepository.findAll(pageable).map(crawlerMapper::toDtoNoSources);
    }

    public Optional<CrawlerDTO> findById(Long id) {
        return crawlerRepository.findById(id).map(crawlerMapper::toDtoNoSources);
    }

    public void deleteById(Long id) {
        crawlerRepository.deleteById(id);
    }

    public boolean isCrawlerExistsById(Long id) {
        return crawlerRepository.existsById(id);
    }

    public Optional<CrawlerDTO> updatePartialCrawler(CrawlerDTO crawlerDTO) {
        validateCustomFilters(crawlerDTO);
        return crawlerRepository
            .findById(crawlerDTO.getId())
            .map(existingCrawler -> crawlerPartialUpdateUtility.partialUpdate(existingCrawler, crawlerDTO))
            .map(crawlerRepository::save)
            .map(crawlerMapper::toDto);
    }

    protected CrawlerDTO saveCrawler(final CrawlerDTO crawlerDTO, List<SourceDTO> sources) {
        Crawler crawler = crawlerMapper.toEntity(crawlerDTO, sources);
        if (!isEmpty(crawler.getParserFilters())) {
            throw new UnprocessableEntityException(PARSER_FILTERS_DEPRECATED, ENTITY_NAME, PARSER_FILTERS);
        }
        return crawlerMapper.toDto(crawlerRepository.save(crawler));
    }

    private void validateCustomFilters(CrawlerDTO crawlerDTO) {
        validateUrlFilters(crawlerDTO);
        validateParseFilters(crawlerDTO);
    }

    protected void validateUrlFilters(CrawlerDTO crawlerDTO) {
        if (isEmpty(crawlerDTO.getUrlFilters())) {
            return;
        }
        checkDuplicatedCustomFilters(crawlerDTO.getUrlFilters(), "UrlFilters");
        ValidationFilterResult validationResult = playgroundValidationService.validateUrlFilters(
            new ArrayList<>(crawlerDTO.getUrlFilters())
        );
        if (hasValidationErrors(validationResult)) {
            log.error(" Input URL Filters are not valid Entity: {} Error {} ", ENTITY_NAME, validationResult);
            throw new BadRequestAlertException("Input URL Filters are not valid", ENTITY_NAME, "idvalidation");
        }
    }

    protected void validateParseFilters(CrawlerDTO crawlerDTO) {
        if (isEmpty(crawlerDTO.getParseFilters())) {
            return;
        }
        checkDuplicatedCustomFilters(crawlerDTO.getParseFilters(), "ParseFilters");
        ValidationFilterResult validationResult = playgroundValidationService.validateParseFilters(
            new ArrayList<>(crawlerDTO.getParseFilters())
        );
        if (hasValidationErrors(validationResult)) {
            log.error(" Input Parse Filters are not valid Entity: {} Error {} ", ENTITY_NAME, validationResult);
            throw new BadRequestAlertException("Input Parse Filters are not valid", ENTITY_NAME, "idvalidation");
        }
    }

    protected boolean hasValidationErrors(ValidationFilterResult validationResult) {
        return validationResult.getConfigurations().stream().anyMatch(config -> !isEmpty(config.getValidationErrors()));
    }

    protected void checkDuplicatedCustomFilters(Set<? extends CustomFilter> customFilters, String filterName) {
        Set<String> duplicatedFilters = customFilters
            .stream()
            .collect(groupingBy(CustomFilter::getFilterId, Collectors.counting()))
            .entrySet()
            .stream()
            .filter(map -> map.getValue() > 1)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());

        if (!isEmpty(duplicatedFilters)) {
            String duplicatedFiltersString = String.join(",", duplicatedFilters);
            log.error(" Duplicated {} : {} ", filterName, duplicatedFiltersString);
            throw new IllegalArgumentException(" Duplicated " + filterName + " : " + duplicatedFiltersString);
        }
    }

    private Crawler updateCrawler(CrawlerDTO crawlerDTO, Crawler crawler) {
        crawlerMapper.updateCrawlerDtoToCrawler(crawlerDTO, crawler);
        if (isEmpty(crawler.getParserFilters()) && !isEmpty(crawlerDTO.getParserFilters())) {
            throw new UnprocessableEntityException(PARSER_FILTERS_DEPRECATED, ENTITY_NAME, PARSER_FILTERS);
        }
        Optional.ofNullable(crawler.getUrlFilters()).ifPresent(urlFilter -> mapUrlFilter(urlFilter, crawlerDTO.getUrlFilters()));
        Optional.ofNullable(crawler.getParseFilters()).ifPresent(parseFilter -> mapWihpParseFilter(parseFilter, crawlerDTO.getParseFilters()));
        return crawlerRepository.save(crawler);
    }

    protected void map(Set<ParserFilter> parserFilters, Set<ParserFilterDTO> parserFilterDTOS) {
        parserFilters.forEach(f -> findAndUpdate(parserFilterDTOS, f));
    }

    private void findAndUpdate(Set<ParserFilterDTO> parserFilterDTOS, ParserFilter f) {
        findDto(parserFilterDTOS, f).ifPresent(dto -> parserFilterMapper.filterDtoToFilter(dto, f));
    }

    private Optional<ParserFilterDTO> findDto(Set<ParserFilterDTO> parserFilterDTOS, ParserFilter f) {
        return parserFilterDTOS.stream().filter(dto -> f.getId().equals(dto.getId())).findFirst();
    }

    protected void mapUrlFilter(Set<UrlFilter> urlFilters, Set<UrlFilterDTO> urlFilterDTOS) {
        urlFilters.forEach(f -> findAndUpdateUrlFilters(urlFilterDTOS, f));
    }

    private void findAndUpdateUrlFilters(Set<UrlFilterDTO> urlFilterDTOS, UrlFilter f) {
        findDtoUrlFilter(urlFilterDTOS, f).ifPresent(dto -> urlFilterMapper.urlFilterDtoToUrlFilter(dto, f));
    }

    private Optional<UrlFilterDTO> findDtoUrlFilter(Set<UrlFilterDTO> urlFilterDTOS, UrlFilter urlFilter) {
        return urlFilterDTOS.stream().filter(urlFilterDTO -> urlFilter.getFilterId().equals(urlFilterDTO.getFilterId())).findFirst();
    }

    protected void mapWihpParseFilter(Set<ParseFilter> parseFilters, Set<ParseFilterDTO> parseFilterDTOS) {
        parseFilters.forEach(f -> findAndUpdateParseFilters(parseFilterDTOS, f));
    }

    private void findAndUpdateParseFilters(Set<ParseFilterDTO> parseFilterDTOS, ParseFilter f) {
        findDtoParseFilter(parseFilterDTOS, f).ifPresent(dto -> parseFilterMapper.parseFilterDtoToParseFilter(dto, f));
    }

    private Optional<ParseFilterDTO> findDtoParseFilter(Set<ParseFilterDTO> parseFilterDTOS, ParseFilter parseFilter) {
        return parseFilterDTOS
            .stream()
            .filter(parseFilterDTO -> parseFilter.getFilterId().equals(parseFilterDTO.getFilterId()))
            .findFirst();
    }

    private boolean hasAnyViolation(CrawlerResponseDTO response) {
        return !isEmpty(response.getFieldErrors()) || !isEmpty(response.getSourceErrors());
    }
}
