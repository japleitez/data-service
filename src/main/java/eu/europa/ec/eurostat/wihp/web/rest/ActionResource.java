package eu.europa.ec.eurostat.wihp.web.rest;

import eu.europa.ec.eurostat.wihp.domain.Action;
import eu.europa.ec.eurostat.wihp.http.CustomHeader;
import eu.europa.ec.eurostat.wihp.service.ActionService;
import eu.europa.ec.eurostat.wihp.service.dto.ActionDTO;
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

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link Action}.
 */
@RestController
@RequestMapping("/api")
public class ActionResource {

    private final Logger log = LoggerFactory.getLogger(ActionResource.class);

    private final ActionService actionService;

    public ActionResource(ActionService actionService) {
        this.actionService = actionService;
    }

    /**
     * {@code GET  /actions} : get all the actions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of actions in body.
     */
    @GetMapping("/actions")
    public ResponseEntity<List<ActionDTO>> getAllActions(Pageable pageable) {
        log.debug("REST request to get all Actions");
        Page<ActionDTO> page = actionService.findAll(pageable);
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
     * {@code GET  /actions/:id} : get the "id" actions.
     *
     * @param id the id of the actionsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the actionsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/actions/{id}")
    public ResponseEntity<ActionDTO> getActions(@PathVariable Long id) {
        log.debug("REST request to get Actions : {}", id);
        Optional<ActionDTO> actionsDTO = actionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(actionsDTO);
    }
}
