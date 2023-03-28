package eu.europa.ec.eurostat.wihp.service.acquisitions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.europa.ec.eurostat.wihp.domain.Acquisition;
import eu.europa.ec.eurostat.wihp.domain.Crawler;
import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionAction;
import eu.europa.ec.eurostat.wihp.repository.AcquisitionRepository;
import eu.europa.ec.eurostat.wihp.repository.CrawlerRepository;
import eu.europa.ec.eurostat.wihp.repository.ReportRepository;
import eu.europa.ec.eurostat.wihp.repository.SourceRepository;
import eu.europa.ec.eurostat.wihp.service.AcquisitionActionService;
import eu.europa.ec.eurostat.wihp.service.dto.AcquisitionDTO;
import eu.europa.ec.eurostat.wihp.service.dto.ActionDTO;
import eu.europa.ec.eurostat.wihp.service.dto.CreateAcquisitionDTO;
import eu.europa.ec.eurostat.wihp.service.impl.AcquisitionServiceImpl;
import eu.europa.ec.eurostat.wihp.service.mapper.AcquisitionMapper;
import eu.europa.ec.eurostat.wihp.service.mapper.ReportMapper;
import eu.europa.ec.eurostat.wihp.web.rest.AcquisitionResourceIT;
import eu.europa.ec.eurostat.wihp.web.rest.CrawlerResourceIT;
import eu.europa.ec.eurostat.wihp.web.rest.SourceResourceIT;
import eu.europa.ec.eurostat.wihp.web.rest.errors.UnprocessableEntityException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CreateAcquisitionServiceTest {

    private AcquisitionServiceImpl unit;

    @Mock
    public AcquisitionRepository mockAcquisitionRepository;

    @Mock
    public CrawlerRepository mockCrawlerRepository;

    @Mock
    public SourceRepository mockSourceRepository;

    @Mock
    public ReportRepository reportRepository;

    @Mock
    private ReportMapper reportMapper;

    @Mock
    private AcquisitionMapper acquisitionMapper;

    @Mock
    private AcquisitionActionService acquisitionActionService;

    @Mock(answer = Answers.RETURNS_MOCKS)
    private Process process;

    private Crawler crawler;

    private final Acquisition acquisition = AcquisitionResourceIT.generateAcquisition(UUID.randomUUID());

    @BeforeEach
    public void setUp() {
        unit =
            new AcquisitionServiceImpl(
                mockAcquisitionRepository,
                mockCrawlerRepository,
                reportRepository,
                reportMapper,
                acquisitionActionService,
                acquisitionMapper,
                mockSourceRepository
            );
    }

    @Test
    public void whenCrawlerHasNoSources_thenThrow() {
        CreateAcquisitionDTO createAcquisitionDTO = setupCreateAcquisitionDTO(false);

        when(mockCrawlerRepository.findOneByName(any(String.class))).thenReturn(Optional.of(crawler));
        when(mockSourceRepository.existsSourceByCrawlers(any(Crawler.class))).thenReturn(false);

        Assertions.assertThrows(
            UnprocessableEntityException.class,
            () -> unit.submitAcquisition(createAcquisitionDTO.getName(), createAcquisitionDTO.getUuid())
        );
    }

    @Test
    public void whenCrawlerPresentReturnAcquisition() {
        //GIVEN
        CreateAcquisitionDTO createAcquisitionDTO = setupCreateAcquisitionDTO(true);
        when(mockCrawlerRepository.findOneByName(any(String.class))).thenReturn(Optional.of(crawler));
        when(mockAcquisitionRepository.save(any(Acquisition.class))).thenReturn(acquisition);
        when(acquisitionActionService.execute(acquisition.getId(), AcquisitionAction.SUBMIT)).thenReturn(new ActionDTO());
        when(acquisitionMapper.toDto(any(Acquisition.class))).thenReturn(new AcquisitionDTO());
        when(mockSourceRepository.existsSourceByCrawlers(any(Crawler.class))).thenReturn(true);

        //WHEN
        AcquisitionDTO result = unit.submitAcquisition(createAcquisitionDTO.getName(), createAcquisitionDTO.getUuid());

        //THEN
        assertNotNull(result);
        verify(acquisitionMapper).toDto(any(Acquisition.class));
        verify(mockCrawlerRepository).findOneByName(any(String.class));
        verify(mockAcquisitionRepository).save(any(Acquisition.class));
    }

    @Test
    public void whenCrawlerNotPresent_thenThrow() {
        Assertions.assertThrows(UnprocessableEntityException.class, () -> unit.submitAcquisition("Er8_ __ ", UUID.randomUUID()));
    }

    private CreateAcquisitionDTO setupCreateAcquisitionDTO(boolean hasSource) {
        long id = 1L;

        crawler = CrawlerResourceIT.createCrawlerEntity();
        if (hasSource) {
            crawler.addSource(SourceResourceIT.createEntity(null));
        }
        crawler.setId(id);
        acquisition.setCrawler(crawler);
        return AcquisitionResourceIT.createCreateAcquisitionDTO(crawler.getName());
    }
}
