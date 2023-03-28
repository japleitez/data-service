package eu.europa.ec.eurostat.wihp.service.impl;

import eu.europa.ec.eurostat.wihp.domain.Acquisition;
import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.domain.Report;
import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionAction;
import eu.europa.ec.eurostat.wihp.repository.AcquisitionRepository;
import eu.europa.ec.eurostat.wihp.repository.CrawlerRepository;
import eu.europa.ec.eurostat.wihp.repository.ReportRepository;
import eu.europa.ec.eurostat.wihp.repository.SourceRepository;
import eu.europa.ec.eurostat.wihp.service.AcquisitionActionService;
import eu.europa.ec.eurostat.wihp.service.AcquisitionService;
import eu.europa.ec.eurostat.wihp.service.dto.AcquisitionDTO;
import eu.europa.ec.eurostat.wihp.service.dto.ReportDTO;
import eu.europa.ec.eurostat.wihp.service.dto.StormReportDTO;
import eu.europa.ec.eurostat.wihp.service.mapper.AcquisitionMapper;
import eu.europa.ec.eurostat.wihp.service.mapper.ReportMapper;
import eu.europa.ec.eurostat.wihp.util.JsonNodeUtils;
import eu.europa.ec.eurostat.wihp.web.rest.errors.UnprocessableEntityException;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Acquisition}.
 */
@Service
@Transactional
public class AcquisitionServiceImpl implements AcquisitionService {

    private final Logger log = LoggerFactory.getLogger(AcquisitionServiceImpl.class);

    public static final String ENTITY_NAME = "dataAcquisitionServiceAcquisition";

    private final AcquisitionRepository acquisitionRepository;
    private final CrawlerRepository crawlerRepository;
    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final AcquisitionMapper acquisitionMapper;
    private final SourceRepository sourceRepository;

    private final AcquisitionActionService acquisitionActionService;

    public AcquisitionServiceImpl(
        final AcquisitionRepository acquisitionRepository,
        final CrawlerRepository crawlerRepository,
        final ReportRepository reportRepository,
        final ReportMapper reportMapper,
        final AcquisitionActionService acquisitionActionService,
        final AcquisitionMapper acquisitionMapper,
        final SourceRepository sourceRepository
    ) {
        this.acquisitionRepository = acquisitionRepository;
        this.crawlerRepository = crawlerRepository;
        this.reportRepository = reportRepository;
        this.reportMapper = reportMapper;
        this.acquisitionMapper = acquisitionMapper;
        this.acquisitionActionService = acquisitionActionService;
        this.sourceRepository = sourceRepository;
    }

    @Override
    public AcquisitionDTO submitAcquisition(final String crawlerName, final UUID workflowUuid) {
        Crawler crawler = crawlerRepository
            .findOneByName(crawlerName)
            .orElseThrow(() -> new UnprocessableEntityException("An acquisition must reference a crawler", ENTITY_NAME, "submitopology"));

        if (!sourceRepository.existsSourceByCrawlers(crawler)) {
            throw new UnprocessableEntityException(
                "An acquisition with an empty Source List cannot be submitted",
                ENTITY_NAME,
                "submitopology"
            );
        }

        return createAcquisition(workflowUuid, crawler);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Acquisition> findAll(Pageable pageable) {
        log.debug("Request to get all Acquisitions");
        return acquisitionRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Acquisition> findOne(Long id) {
        log.debug("Request to get Acquisition : {}", id);
        return acquisitionRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Acquisition : {}", id);
        acquisitionRepository.deleteById(id);
    }

    @Override
    public Optional<ReportDTO> updateAcquisitionStatus(final Long id, final StormReportDTO report) {
        return acquisitionRepository.findById(id).map(acquisition -> updateAcquisitionStatus(acquisition, report)).map(reportMapper::toDto);
    }

    private Report updateAcquisitionStatus(final Acquisition acquisition, final StormReportDTO reportDTO) {
        acquisition.setStormId(reportDTO.getStormId());
        acquisition.updateStatus(reportDTO.getStatus());
        acquisitionRepository.save(acquisition);
        Report report = new Report(acquisition, JsonNodeUtils.createJsonNode(reportDTO));
        return reportRepository.save(report);
    }

    private AcquisitionDTO createAcquisition(UUID workflowUuid, Crawler crawler) {
        final Acquisition acquisition = acquisitionRepository.save(new Acquisition(workflowUuid, crawler));
        acquisitionActionService.execute(acquisition.getId(), AcquisitionAction.SUBMIT);
        return acquisitionMapper.toDto(acquisition);
    }
}
