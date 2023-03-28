package eu.europa.ec.eurostat.wihp.service;

import eu.europa.ec.eurostat.wihp.domain.Action;
import eu.europa.ec.eurostat.wihp.repository.ActionRepository;
import eu.europa.ec.eurostat.wihp.service.dto.ActionDTO;
import eu.europa.ec.eurostat.wihp.service.mapper.ActionMapperImpl;
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
public class ActionServiceTest {

    private ActionService actionService;

    @Mock
    private ActionRepository actionRepository;

    @BeforeEach
    public void setUp() {
        actionService = new ActionService(actionRepository, new ActionMapperImpl());
    }

    @Test
    public void findOne_test() {
        when(actionRepository.findById(any(Long.class))).thenReturn(Optional.of(getTestAction(22L)));
        assertEquals(22, actionService.findOne(1L).orElseThrow().getId().longValue());
    }

    @Test
    public void save_test() {
        when(actionRepository.save(any(Action.class))).thenReturn(getTestAction(23L));
        assertEquals(23, actionService.save(getTestActiontDTO(23L)).getId().longValue());
    }

    private Action getTestAction(Long id) {
        return new Action().id(id);
    }

    private ActionDTO getTestActiontDTO(Long id) {
        ActionDTO action = new ActionDTO();
        action.setId(id);
        return action;
    }
}
