package eu.europa.ec.eurostat.wihp.service;

import eu.europa.ec.eurostat.wihp.domain.ActionLog;
import eu.europa.ec.eurostat.wihp.repository.ActionLogRepository;
import eu.europa.ec.eurostat.wihp.service.dto.ActionLogDTO;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import eu.europa.ec.eurostat.wihp.service.mapper.ActionLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ActionLog}.
 */
@Service
@Transactional
public class ActionLogService {

    private final Logger log = LoggerFactory.getLogger(ActionLogService.class);

    private final ActionLogRepository actionLogRepository;
    private final ActionLogMapper actionLogMapper;

    public ActionLogService(final ActionLogRepository actionLogRepository,final ActionLogMapper actionLogMapper) {
        this.actionLogRepository = actionLogRepository;
        this.actionLogMapper = actionLogMapper;
    }

    /**
     * Save a actionLogs.
     *
     * @param actionLogDTO the entity to save.
     * @return the persisted entity.
     */
    public ActionLogDTO save(ActionLogDTO actionLogDTO) {
        log.debug("Request to save ActionLogs : {}", actionLogDTO);
        ActionLog actionLog = actionLogMapper.toEntity(actionLogDTO);
        actionLog = actionLogRepository.save(actionLog);
        return actionLogMapper.toDto(actionLog);
    }

    /**
     * Get all the actionLogs.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ActionLogDTO> findAll() {
        log.debug("Request to get all ActionLogs");
        return actionLogRepository.findAll().stream().map(actionLogMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Transactional(readOnly = true)
    public Page<ActionLogDTO> findAll(Pageable pageable) {
        return actionLogRepository.findAll(pageable).map(actionLogMapper::toDto);
    }

    /**
     * Get one actionLogs by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ActionLogDTO> findOne(Long id) {
        log.debug("Request to get ActionLogs : {}", id);
        return actionLogRepository.findById(id).map(actionLogMapper::toDto);
    }

    /**
     * Delete the actionLogs by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ActionLogs : {}", id);
        actionLogRepository.deleteById(id);
    }
}
