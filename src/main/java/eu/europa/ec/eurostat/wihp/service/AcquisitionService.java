package eu.europa.ec.eurostat.wihp.service;

import eu.europa.ec.eurostat.wihp.domain.Acquisition;
import eu.europa.ec.eurostat.wihp.service.dto.AcquisitionDTO;
import eu.europa.ec.eurostat.wihp.service.dto.ReportDTO;
import eu.europa.ec.eurostat.wihp.service.dto.StormReportDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Service Interface for managing {@link Acquisition}.
 */
public interface AcquisitionService {
    /**
     * Get all the acquisitions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Acquisition> findAll(Pageable pageable);

    /**
     * Get the "id" acquisition.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Acquisition> findOne(Long id);

    /**
     * Delete the "id" acquisition.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Submit/deploy acquisition to Apache Storm
     *
     * @param crawlerName  the crawler name for the acquisition to submit
     * @param workflowUuid the workflow workflowUuid
     */
    AcquisitionDTO submitAcquisition(String crawlerName, UUID workflowUuid);

    Optional<ReportDTO> updateAcquisitionStatus(Long id, StormReportDTO report);
}
