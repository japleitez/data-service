package eu.europa.ec.eurostat.wihp.web.rest;

import eu.europa.ec.eurostat.wihp.http.CustomHeader;
import eu.europa.ec.eurostat.wihp.service.crawlers.CrawlerService;
import eu.europa.ec.eurostat.wihp.service.dto.CrawlerDTO;
import eu.europa.ec.eurostat.wihp.service.mapper.MultiPartFileMapper;
import eu.europa.ec.eurostat.wihp.web.rest.errors.BadRequestAlertException;
import eu.europa.ec.eurostat.wihp.web.rest.model.CopyCrawlerRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link eu.europa.ec.eurostat.wihp.domain.Crawler}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CrawlerResource {

    public static final String ENTITY_NAME = "dataAcquisitionServiceCrawler";

    private final Logger log = LoggerFactory.getLogger(CrawlerResource.class);
    private final CrawlerService crawlerService;
    private final MultiPartFileMapper multiPartFileMapper;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public CrawlerResource(CrawlerService crawlerService, MultiPartFileMapper importSourcesMapper) {
        this.crawlerService = crawlerService;
        this.multiPartFileMapper = importSourcesMapper;
    }

    /**
     * {@code POST  /crawlers} : Create a new crawler.
     *
     * @param crawlerDTO the crawler to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new crawler, or with status {@code 400 (Bad Request)} if the crawler has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/crawlers")
    public ResponseEntity<CrawlerDTO> createCrawler(@Valid @RequestBody CrawlerDTO crawlerDTO) throws URISyntaxException {
        log.debug("REST request to save Crawler : {}", crawlerDTO);
        if (crawlerDTO.getId() != null) {
            throw new BadRequestAlertException("A new crawler cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CrawlerDTO result = crawlerService.createCrawler(crawlerDTO);
        return ResponseEntity
            .created(new URI("/api/crawlers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /crawlers/:id} : Updates an existing crawler.
     *
     * @param id         the id of the crawler to save.
     * @param crawlerDTO the crawler to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated crawler,
     * or with status {@code 400 (Bad Request)} if the crawler is not valid,
     * or with status {@code 500 (Internal Server Error)} if the crawler couldn't be updated.
     */
    @PutMapping("/crawlers/{id}")
    public ResponseEntity<CrawlerDTO> updateCrawler(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CrawlerDTO crawlerDTO
    ) {
        log.debug("REST request to update Crawler : {}, {}", id, crawlerDTO);
        validateCrawlerUpdateRequest(id, crawlerDTO);
        CrawlerDTO result = crawlerService.updateCrawler(crawlerDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, crawlerDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /crawlers/:id} : Partial updates given fields of an existing crawler, field will ignore if it is null
     *
     * @param id         the id of the crawler to save.
     * @param crawlerDTO the crawler to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated crawler,
     * or with status {@code 400 (Bad Request)} if the crawler is not valid,
     * or with status {@code 404 (Not Found)} if the crawler is not found,
     * or with status {@code 500 (Internal Server Error)} if the crawler couldn't be updated.
     */
    @PatchMapping(value = "/crawlers/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<CrawlerDTO> partialUpdateCrawler(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CrawlerDTO crawlerDTO
    ) {
        log.debug("REST request to partial update Crawler partially : {}, {}", id, crawlerDTO);
        validateCrawlerUpdateRequest(id, crawlerDTO);
        Optional<CrawlerDTO> result = crawlerService.updatePartialCrawler(crawlerDTO);
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, crawlerDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /crawlers} : get all the crawlers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of crawlers in body.
     */
    @GetMapping("/crawlers")
    public ResponseEntity<List<CrawlerDTO>> getAllCrawlers(Pageable pageable) {
        log.debug("REST request to get a page of Crawlers");
        Page<CrawlerDTO> page = crawlerService.getPageableCrawlerList(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        if (pageable.getPageNumber() > page.getTotalPages() - 1) {
            headers.add(CustomHeader.X_HTTP_ERROR_DESCRIPTION, String.format("Page limit exceeded; total=%d", page.getTotalPages()));
            return ResponseEntity.badRequest().headers(headers).body(page.getContent());
        } else {
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
    }

    /**
     * {@code GET  /crawlers/:id} : get the "id" crawler.
     *
     * @param id the id of the crawler to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the crawler, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/crawlers/{id}")
    public ResponseEntity<CrawlerDTO> getCrawler(@PathVariable Long id) {
        log.debug("REST request to get Crawler : {}", id);
        return ResponseUtil.wrapOrNotFound(crawlerService.findById(id));
    }

    /**
     * {@code DELETE  /crawlers/:id} : delete the "id" crawler.
     *
     * @param id the id of the crawler to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/crawlers/{id}")
    public ResponseEntity<Void> deleteCrawler(@PathVariable Long id) {
        log.debug("REST request to delete Crawler : {}", id);
        crawlerService.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping("/crawlers/{id}/copy")
    public ResponseEntity<CrawlerDTO> copyCrawler(@PathVariable Long id, @Valid @RequestBody CopyCrawlerRequest copyCrawlerRequest)
        throws URISyntaxException {
        log.debug("REST request to copy Crawler : {}, {}", id, copyCrawlerRequest);
        // validation
        if (id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        String name = copyCrawlerRequest.getName();
        if (!StringUtils.hasText(name)) {
            throw new BadRequestAlertException("Invalid name", ENTITY_NAME, "namenull");
        }

        CrawlerDTO result = crawlerService
            .copyCrawler(id, name)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));

        return ResponseEntity
            .created(new URI("/api/crawlers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PostMapping("/crawlers/import")
    public ResponseEntity<String> importCrawler(@Valid @RequestParam("fileToUpload0") MultipartFile file) throws URISyntaxException {
        log.debug("REST request to import crawler file: {}", file.getName());

        CrawlerDTO crawlerDTO = multiPartFileMapper.convert(file, CrawlerDTO.class);
        CrawlerDTO crawler = crawlerService.persistRequest(crawlerDTO);

        return ResponseEntity
            .created(new URI("/api/crawlers/import"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, crawler.getId().toString()))
            .body(String.format("{\"crawler\": \"/api/crawlers/import/%s\"}", crawler.getId().toString()));
    }

    private void validateCrawlerUpdateRequest(Long id, CrawlerDTO crawlerDTO) {
        if (null == crawlerDTO.getId()) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, crawlerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!crawlerService.isCrawlerExistsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
    }
}
