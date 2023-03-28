package eu.europa.ec.eurostat.wihp.web.rest;

import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionAction;
import eu.europa.ec.eurostat.wihp.service.AcquisitionActionService;
import eu.europa.ec.eurostat.wihp.service.AcquisitionService;
import eu.europa.ec.eurostat.wihp.service.dto.*;
import eu.europa.ec.eurostat.wihp.service.impl.AcquisitionServiceImpl;
import eu.europa.ec.eurostat.wihp.service.mapper.AcquisitionMapper;
import eu.europa.ec.eurostat.wihp.web.rest.errors.BadRequestAlertException;
import eu.europa.ec.eurostat.wihp.web.rest.errors.UnprocessableEntityException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link eu.europa.ec.eurostat.wihp.domain.Acquisition}.
 */
@RestController
@RequestMapping("/api")
public class AcquisitionResource {

    private final Logger log = LoggerFactory.getLogger(AcquisitionResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AcquisitionService acquisitionService;

    private final AcquisitionActionService acquisitionActionService;

    private final AcquisitionMapper acquisitionMapper;

    public AcquisitionResource(
        final AcquisitionService acquisitionService,
        final AcquisitionActionService acquisitionActionService,
        final AcquisitionMapper acquisitionMapper
    ) {
        this.acquisitionService = acquisitionService;
        this.acquisitionActionService = acquisitionActionService;
        this.acquisitionMapper = acquisitionMapper;
    }

    /**
     * {@code POST  /acquisitions} : Create a new acquisition.
     *
     * @param createAcquisitionDTO the acquisition to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new acquisition, or with status {@code 400 (Bad Request)} if the acquisition has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/acquisitions")
    public ResponseEntity<AcquisitionDTO> createAcquisition(@RequestBody CreateAcquisitionDTO createAcquisitionDTO)
        throws URISyntaxException {
        log.debug("REST request to save AcquisitionDTO : {}", createAcquisitionDTO);
        if (StringUtils.isEmpty(createAcquisitionDTO.getName())) {
            throw new BadRequestAlertException("Invalid name ", AcquisitionServiceImpl.ENTITY_NAME, "nameinvalid");
        }
        if (createAcquisitionDTO.getUuid() == null) {
            throw new UnprocessableEntityException("Invalid uuid ", AcquisitionServiceImpl.ENTITY_NAME, "uuidinvalid");
        }

        AcquisitionDTO result = acquisitionService.submitAcquisition(createAcquisitionDTO.getName(), createAcquisitionDTO.getUuid());

        return ResponseEntity
            .created(new URI("/api/acquisitions/" + result.getId()))
            .headers(
                HeaderUtil.createEntityCreationAlert(applicationName, true, AcquisitionServiceImpl.ENTITY_NAME, result.getId().toString())
            )
            .body(result);
    }

    /**
     * {@code GET  /acquisitions} : get all the acquisitions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of acquisitions in body.
     */
    @GetMapping("/acquisitions")
    public ResponseEntity<List<AcquisitionDTO>> getAllAcquisitions(Pageable pageable) {
        log.debug("REST request to get a page of Acquisitions");
        Page<AcquisitionDTO> page = acquisitionService.findAll(pageable).map(acquisitionMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /acquisitions/:id} : get the "id" acquisition.
     *
     * @param id the id of the acquisition to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the acquisition, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/acquisitions/{id}")
    public ResponseEntity<AcquisitionDTO> getAcquisition(@PathVariable Long id) {
        log.debug("REST request to get Acquisition : {}", id);
        Optional<AcquisitionDTO> acquisition = acquisitionService.findOne(id).map(acquisitionMapper::toDto);
        return ResponseUtil.wrapOrNotFound(acquisition);
    }

    /**
     * {@code DELETE  /acquisitions/:id} : delete the "id" acquisition.
     *
     * @param id the id of the acquisition to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/acquisitions/{id}")
    public ResponseEntity<Void> deleteAcquisition(@PathVariable Long id) {
        log.debug("REST request to delete Acquisition : {}", id);
        acquisitionService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, AcquisitionServiceImpl.ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping("/acquisitions/{id}/action/{action}")
    public ResponseEntity<ActionDTO> executeAcquisitionAction(@PathVariable Long id, @PathVariable AcquisitionAction action) {
        log.debug("REST request to execute action {} to Acquisition {}", action, id);
        ActionDTO result = acquisitionActionService.execute(id, action);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/acquisitions/{id}/report")
    public ResponseEntity<ReportDTO> updateAcquisitionStatus(@PathVariable Long id, @RequestBody StormReportDTO report) {
        log.debug("REST request to update Acquisition {} status from report {},", id, report);
        Optional<ReportDTO> reportDTO = acquisitionService.updateAcquisitionStatus(id, report);
        return ResponseUtil.wrapOrNotFound(reportDTO);
    }
}
