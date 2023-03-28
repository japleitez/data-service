package eu.europa.ec.eurostat.wihp.web.rest;

import eu.europa.ec.eurostat.wihp.domain.ActionLog;
import eu.europa.ec.eurostat.wihp.http.CustomHeader;
import eu.europa.ec.eurostat.wihp.service.ActionLogService;
import eu.europa.ec.eurostat.wihp.service.dto.ActionLogDTO;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ActionLog}.
 */
@RestController
@RequestMapping("/api")
public class ActionLogResource {

    private final Logger log = LoggerFactory.getLogger(ActionLogResource.class);

    private final ActionLogService actionLogService;

    public ActionLogResource(ActionLogService actionLogService) {
        this.actionLogService = actionLogService;
    }

    /**
     * {@code GET  /action-logs} : get all the actionLogs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of actionLogs in body.
     */
    @GetMapping("/action-logs")
    public ResponseEntity<List<ActionLogDTO>> getAllActionLogs(Pageable pageable) {
        log.debug("REST request to get all ActionLogs");
        Page<ActionLogDTO> page = actionLogService.findAll(pageable);
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
     * {@code GET  /action-logs/:id} : get the "id" actionLogs.
     *
     * @param id the id of the actionLogsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the actionLogsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/action-logs/{id}")
    public ResponseEntity<ActionLogDTO> getActionLogs(@PathVariable Long id) {
        log.debug("REST request to get ActionLogs : {}", id);
        Optional<ActionLogDTO> actionLogsDTO = actionLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(actionLogsDTO);
    }
}
