package eu.europa.ec.eurostat.wihp.web.rest;

import static eu.europa.ec.eurostat.wihp.service.crawlers.CrawlerSourceService.ENTITY_NAME;

import eu.europa.ec.eurostat.wihp.domain.Source;
import eu.europa.ec.eurostat.wihp.http.CustomHeader;
import eu.europa.ec.eurostat.wihp.service.crawlers.CrawlerSourceService;
import eu.europa.ec.eurostat.wihp.service.dto.CrawlerDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api")
@Transactional
public class CrawlerSourceResource {

    private final Logger log = LoggerFactory.getLogger(CrawlerSourceResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CrawlerSourceService crawlerSourceService;

    public CrawlerSourceResource(CrawlerSourceService crawlerSourceService) {
        this.crawlerSourceService = crawlerSourceService;
    }

    /**
     * {@code GET  /sources} : get all the sources paginated.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sources in body.
     */
    @GetMapping("/crawlers/{id}/sources")
    public ResponseEntity<List<Source>> getCrawlerSources(@PathVariable Long id, Pageable pageable) {
        log.debug("REST request to get a page of Sources");

        Page<Source> page = crawlerSourceService.getCrawlerSourcesPaginated(id, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        if (!page.getContent().isEmpty() && pageable.getPageNumber() > page.getTotalPages() - 1) {
            String httpErrorDescription = String.format("Page limit exceeded; total=%d", page.getTotalPages());
            headers.add(CustomHeader.X_HTTP_ERROR_DESCRIPTION, httpErrorDescription);
            return ResponseEntity.badRequest().headers(headers).body(page.getContent());
        }

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code POST  /crawlers} : links an existing source {source_id}  to a Crawler {crawler_id}.
     *
     * @param crawlerId the crawler id.
     * @param sourceId  the source id.
     * @return the {@link ResponseEntity} with status
     * {@code 201 (Created)} and with body the updated crawler,
     * or with status {@code 400 (Bad Request)} if the crawler or the source doesn't exist.
     */
    @PostMapping("/crawlers/{crawlerId}/sources/{sourceId}")
    public ResponseEntity<Void> addSourceToCrawler(@PathVariable Long crawlerId, @PathVariable Long sourceId) {
        log.debug("REST request to link Crawler : {}, with Source : {}", crawlerId, sourceId);

        CrawlerDTO crawlerDTO = crawlerSourceService.addSourceToCrawler(crawlerId, sourceId);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, crawlerDTO.getId().toString()))
            .build();
    }

    /**
     * {@code Delete  /crawlers} : unlink an existing source {source_id}  to a Crawler {crawler_id}.
     *
     * @param crawlerId the crawler id.
     * @param sourceId  the source id.
     * @return the {@link ResponseEntity} with status
     * {@code 201 (Created)} and with body the updated crawler,
     * or with status {@code 400 (Bad Request)} if the crawler or the source doesn't exist.
     */
    @DeleteMapping("/crawlers/{crawlerId}/sources/{sourceId}")
    public ResponseEntity<Void> removeSourceFromCrawler(@PathVariable Long crawlerId, @PathVariable Long sourceId) {
        log.debug("REST request to unlink Crawler : {}, with Source : {}", crawlerId, sourceId);

        CrawlerDTO crawlerDTO = crawlerSourceService.removeSourceFromCrawler(crawlerId, sourceId);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, crawlerDTO.getId().toString()))
            .build();
    }
}
