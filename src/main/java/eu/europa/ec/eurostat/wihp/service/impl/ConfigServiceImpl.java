package eu.europa.ec.eurostat.wihp.service.impl;

import eu.europa.ec.eurostat.wihp.domain.Config;
import eu.europa.ec.eurostat.wihp.repository.AcquisitionRepository;
import eu.europa.ec.eurostat.wihp.repository.ConfigRepository;
import eu.europa.ec.eurostat.wihp.service.ConfigService;
import eu.europa.ec.eurostat.wihp.service.dto.ConfigDTO;
import eu.europa.ec.eurostat.wihp.service.mapper.ConfigMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Config}.
 */
@Service
@Transactional
public class ConfigServiceImpl implements ConfigService {

    private final Logger log = LoggerFactory.getLogger(ConfigServiceImpl.class);

    private final ConfigRepository configRepository;

    private final AcquisitionRepository acquisitionRepository;

    private final ConfigMapper configMapper;

    public ConfigServiceImpl(ConfigRepository configRepository, AcquisitionRepository acquisitionRepository, ConfigMapper configMapper) {
        this.configRepository = configRepository;
        this.acquisitionRepository = acquisitionRepository;
        this.configMapper = configMapper;
    }

    @Override
    public Optional<ConfigDTO> save(ConfigDTO configDTO) {
        log.debug("Request to save Config : {}", configDTO);
        return acquisitionRepository
            .findOne(configDTO.getAcquisitionId())
            .map(acquisition -> configMapper.toConfig(configDTO, acquisition))
            .map(configRepository::save)
            .map(configMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConfigDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Configs");
        return configRepository.findAll(pageable).map(configMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ConfigDTO> findOne(Long id) {
        log.debug("Request to get Config : {}", id);
        return configRepository.findById(id).map(configMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Config : {}", id);
        configRepository.deleteById(id);
    }
}
