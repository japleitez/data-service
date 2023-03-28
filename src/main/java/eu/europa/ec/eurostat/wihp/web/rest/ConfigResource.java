package eu.europa.ec.eurostat.wihp.web.rest;

import eu.europa.ec.eurostat.wihp.service.ConfigService;
import eu.europa.ec.eurostat.wihp.service.dto.ConfigDTO;
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
 * REST controller for managing {@link eu.europa.ec.eurostat.wihp.domain.Config}.
 */
@RestController
@RequestMapping("/api")
public class ConfigResource {

    private final Logger log = LoggerFactory.getLogger(ConfigResource.class);

    private final ConfigService configService;

    public ConfigResource(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * {@code GET  /configs} : get all the configs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of configs in body.
     */
    @GetMapping("/configs")
    public ResponseEntity<List<ConfigDTO>> getAllConfigs(Pageable pageable) {
        log.debug("REST request to get a page of Configs");
        Page<ConfigDTO> page = configService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /configs/:id} : get the "id" config.
     *
     * @param id the id of the configDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the configDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/configs/{id}")
    public ResponseEntity<ConfigDTO> getConfig(@PathVariable Long id) {
        log.debug("REST request to get Config : {}", id);
        Optional<ConfigDTO> configDTO = configService.findOne(id);
        return ResponseUtil.wrapOrNotFound(configDTO);
    }
}
