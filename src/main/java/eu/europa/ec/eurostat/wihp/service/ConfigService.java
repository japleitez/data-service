package eu.europa.ec.eurostat.wihp.service;

import eu.europa.ec.eurostat.wihp.service.dto.ConfigDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link eu.europa.ec.eurostat.wihp.domain.Config}.
 */
public interface ConfigService {
    /**
     * Save a config.
     *
     * @param configDTO the entity to save.
     * @return the persisted entity.
     */
    Optional<ConfigDTO> save(ConfigDTO configDTO);

    /**
     * Get all the configs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ConfigDTO> findAll(Pageable pageable);

    /**
     * Get the "id" config.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ConfigDTO> findOne(Long id);

    /**
     * Delete the "id" config.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
