package eu.europa.ec.eurostat.wihp.web.rest;

import eu.europa.ec.eurostat.wihp.http.CustomHeader;
import eu.europa.ec.eurostat.wihp.repository.SourceRepository;
import eu.europa.ec.eurostat.wihp.service.bulk.BulkSourcesService;
import eu.europa.ec.eurostat.wihp.service.dto.SourceDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceListDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceResponseListDTO;
import eu.europa.ec.eurostat.wihp.service.mapper.SourceMapper;
import eu.europa.ec.eurostat.wihp.web.rest.errors.BadRequestAlertException;
import eu.europa.ec.eurostat.wihp.web.rest.errors.UnprocessableEntityException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link eu.europa.ec.eurostat.wihp.domain.Source}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SourceResource {

    public static final String ENTITY_NAME = "dataAcquisitionServiceSource";

    private final Logger log = LoggerFactory.getLogger(SourceResource.class);
    private final SourceRepository sourceRepository;
    private final SourceMapper sourceMapper;
    private final BulkSourcesService bulkSourcesService;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public SourceResource(SourceRepository sourceRepository, SourceMapper sourceMapper, BulkSourcesService bulkSourcesService) {
        this.sourceRepository = sourceRepository;
        this.sourceMapper = sourceMapper;
        this.bulkSourcesService = bulkSourcesService;
    }

    /**
     * {@code POST  /sources} : Create a new source.
     *
     * @param sourceDto the source to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new source, or with status {@code 400 (Bad Request)} if the source has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/sources")
    public ResponseEntity<SourceDTO> createSource(@Valid @RequestBody SourceDTO sourceDto) throws URISyntaxException {
        log.debug("REST request to save Source : {}", sourceDto);
        if (sourceDto.getId() != null) {
            throw new BadRequestAlertException("A new source cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SourceDTO result = sourceMapper.toDto(sourceRepository.save(sourceMapper.toEntity(sourceDto)));
        return ResponseEntity
            .created(new URI("/api/sources/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /sources/:id} : Updates an existing source.
     *
     * @param id        the id of the source to save.
     * @param sourceDto the source to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated source,
     * or with status {@code 400 (Bad Request)} if the source is not valid,
     * or with status {@code 500 (Internal Server Error)} if the source couldn't be updated.
     */
    @PutMapping("/sources/{id}")
    public ResponseEntity<SourceDTO> updateSource(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SourceDTO sourceDto
    ) {
        log.debug("REST request to update Source : {}, {}", id, sourceDto);
        validateSourceDto(sourceDto, id);
        SourceDTO result = sourceMapper.toDto(sourceRepository.save(sourceMapper.toEntity(sourceDto)));
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sourceDto.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /sources/:id} : Partial updates given fields of an existing source, field will ignore if it is null
     *
     * @param id        the id of the source to save.
     * @param sourceDto the source to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated source,
     * or with status {@code 400 (Bad Request)} if the source is not valid,
     * or with status {@code 404 (Not Found)} if the source is not found,
     * or with status {@code 500 (Internal Server Error)} if the source couldn't be updated.
     */
    @PatchMapping(value = "/sources/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<SourceDTO> partialUpdateSource(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SourceDTO sourceDto
    ) {
        log.debug("REST request to partial update Source partially : {}, {}", id, sourceDto);
        validateSourceDto(sourceDto, id);
        Optional<SourceDTO> source = sourceRepository
            .findById(sourceDto.getId())
            .map(existingSource -> {
                if (sourceDto.getName() != null) {
                    existingSource.setName(sourceDto.getName());
                }
                if (sourceDto.getUrl() != null) {
                    existingSource.setUrl(sourceDto.getUrl());
                }
                return existingSource;
            })
            .map(sourceRepository::save)
            .map(sourceMapper::toDto);
        return ResponseUtil.wrapOrNotFound(
            source,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sourceDto.getId().toString())
        );
    }

    /**
     * {@code GET  /sources} : get all the sources.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sources in body.
     */
    @GetMapping("/sources")
    public ResponseEntity<List<SourceDTO>> getAllSources(Pageable pageable) {
        log.debug("REST request to get a page of Sources");
        Page<SourceDTO> page = sourceRepository.findAll(pageable).map(sourceMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        if (pageable.getPageNumber() > page.getTotalPages() - 1) {
            String httpErrorDescription = String.format("Page limit exceeded; total=%d", page.getTotalPages());
            headers.add(CustomHeader.X_HTTP_ERROR_DESCRIPTION, httpErrorDescription);
            return ResponseEntity.badRequest().headers(headers).body(page.getContent());
        } else {
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
    }

    /**
     * {@code GET  /sources/:id} : get the "id" source.
     *
     * @param id the id of the source to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the source, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/sources/{id}")
    public ResponseEntity<SourceDTO> getSource(@PathVariable Long id) {
        log.debug("REST request to get Source : {}", id);
        Optional<SourceDTO> source = sourceRepository.findById(id).map(sourceMapper::toDto);
        return ResponseUtil.wrapOrNotFound(source);
    }

    /**
     * {@code DELETE  /sources/:id} : delete the "id" source.
     *
     * @param id the id of the source to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/sources/{id}")
    public ResponseEntity<Void> deleteSource(@PathVariable Long id) {
        log.debug("REST request to delete Source : {}", id);
        sourceRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping("/sources/batch/import")
    public ResponseEntity<SourceResponseListDTO> createMultipleSources(@Valid @RequestParam("fileToUpload0") MultipartFile file) {
        log.debug("REST request to batch insert sources");
        SourceListDTO inModel = bulkSourcesService.convertFile(file);
        if (!bulkSourcesService.isObjectValid(inModel)) {
            log.error("The Input List Cannot be empty");
            throw new UnprocessableEntityException("The Input List Cannot be empty", ENTITY_NAME, "InputInvalid");
        }
        SourceResponseListDTO result = bulkSourcesService.batchImportSources(inModel);
        return result.getSources().isEmpty()
            ? ResponseEntity.status(HttpStatus.CREATED).body(new SourceResponseListDTO())
            : ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
    }

    private void validateSourceDto(SourceDTO sourceDto, Long id) {
        if (sourceDto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sourceDto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!sourceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
    }
}
