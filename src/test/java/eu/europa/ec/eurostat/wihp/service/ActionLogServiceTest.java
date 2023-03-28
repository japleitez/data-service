package eu.europa.ec.eurostat.wihp.service;

import eu.europa.ec.eurostat.wihp.domain.ActionLog;
import eu.europa.ec.eurostat.wihp.repository.ActionLogRepository;
import eu.europa.ec.eurostat.wihp.service.dto.ActionLogDTO;
import eu.europa.ec.eurostat.wihp.service.mapper.ActionLogMapperImpl;
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
public class ActionLogServiceTest {

    private ActionLogService actionLogService;

    @Mock
    private ActionLogRepository actionLogRepository;

    @BeforeEach
    public void setUp() {
        actionLogService = new ActionLogService(actionLogRepository, new ActionLogMapperImpl());
    }

    @Test
    public void findOne_test() {
        when(actionLogRepository.findById(any(Long.class))).thenReturn(Optional.of(getTestActionLog(22L)));
        assertEquals(22, actionLogService.findOne(1L).orElseThrow().getId().longValue());
    }

    @Test
    public void save_test() {
        when(actionLogRepository.save(any(ActionLog.class))).thenReturn(getTestActionLog(24L));
        assertEquals(24, actionLogService.save(getTestActionLogDTO(24L)).getId().longValue());
    }

    private ActionLog getTestActionLog(Long id) {
        return new ActionLog().id(id);
    }

    private ActionLogDTO getTestActionLogDTO(Long id) {
        ActionLogDTO actionLogDTO = new ActionLogDTO();
        actionLogDTO.setId(id);
        return actionLogDTO;
    }
}
