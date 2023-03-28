package eu.europa.ec.eurostat.wihp.service;

import eu.europa.ec.eurostat.wihp.domain.Action;
import eu.europa.ec.eurostat.wihp.repository.ActionRepository;
import eu.europa.ec.eurostat.wihp.service.dto.ActionDTO;
import eu.europa.ec.eurostat.wihp.service.mapper.ActionMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Action}.
 */
@Service
@Transactional
public class ActionService {

    private final Logger log = LoggerFactory.getLogger(ActionService.class);

    private final ActionRepository actionRepository;
    private final ActionMapper actionMapper;


    public ActionService(final ActionRepository actionRepository, final ActionMapper actionMapper) {
        this.actionRepository = actionRepository;
        this.actionMapper = actionMapper;
    }

    /**
     * Save a actions.
     *
     * @param actionDTO the entity to save.
     * @return the persisted entity.
     */
    public ActionDTO save(ActionDTO actionDTO) {
        log.debug("Request to save Actions : {}", actionDTO);
        Action action = actionMapper.toEntity(actionDTO);
        action = actionRepository.save(action);
        return actionMapper.toDto(action);
    }

    /**
     * Get all the actions.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ActionDTO> findAll() {
        log.debug("Request to get all Actions");
        return actionRepository.findAll().stream().map(actionMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Transactional(readOnly = true)
    public Page<ActionDTO> findAll(Pageable pageable) {
        log.debug("Request to get a page of Actions");
        return actionRepository.findAll(pageable).map(actionMapper::toDto);
    }

    /**
     * Get one actions by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ActionDTO> findOne(Long id) {
        log.debug("Request to get Actions : {}", id);
        return actionRepository.findById(id).map(actionMapper::toDto);
    }

    /**
     * Delete the actions by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Actions : {}", id);
        actionRepository.deleteById(id);
    }
}
