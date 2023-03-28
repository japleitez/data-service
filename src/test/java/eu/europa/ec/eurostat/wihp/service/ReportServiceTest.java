package eu.europa.ec.eurostat.wihp.service;

import eu.europa.ec.eurostat.wihp.domain.Report;
import eu.europa.ec.eurostat.wihp.repository.ReportRepository;
import eu.europa.ec.eurostat.wihp.service.dto.ReportDTO;
import eu.europa.ec.eurostat.wihp.service.mapper.ReportMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    private ReportService reportService;

    @Mock
    private ReportRepository reportRepository;

    @BeforeEach
    public void setUp() {
        reportService = new ReportService(reportRepository, new ReportMapperImpl());
    }

    @Test
    public void findOne_test() {
        when(reportRepository.findById(any(Long.class))).thenReturn(Optional.of(getTestReport(22L)));
        assertEquals(22, reportService.findOne(1L).orElseThrow().getId().longValue());
    }

    @Test
    public void findAll_test() {
        when(reportRepository.findAll(any(Pageable.class))).thenReturn(mock(Page.class));
        assertNotNull(reportService.findAll(PageRequest.of(0, 1)));
    }

    @Test
    public void save_test() {
        when(reportRepository.save(any(Report.class))).thenReturn(getTestReport(22L));
        assertEquals(22, reportService.save(getTestReportDTO(22L)).getId().longValue());
    }

    private Report getTestReport(Long id) {
        return new Report().id(id);
    }

    private ReportDTO getTestReportDTO(Long id) {
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setId(id);
        return reportDTO;
    }
}
