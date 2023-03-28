package eu.europa.ec.eurostat.wihp.service.mapper;

import eu.europa.ec.eurostat.wihp.domain.Action;
import eu.europa.ec.eurostat.wihp.domain.ActionLog;
import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionAction;
import eu.europa.ec.eurostat.wihp.service.dto.ActionLogDTO;
import java.time.Instant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

class ActionLogMapperTest {

    private static final Long DEFAULT_ACTION_LOG_ID = 1l;
    private static final Long DEFAULT_ACTION_ID = 2L;
    private static final String DEFAULT_TITLE = "title";
    private static final String DEFAULT_LOG_TEXT = "log-text";



    @Test
    public void checkToDto_thenCopyFields() {
        Action action = new Action().id(DEFAULT_ACTION_ID).date(Instant.now()).success(true).action(AcquisitionAction.PAUSE);
        ActionLog actionLog = new ActionLog().id(DEFAULT_ACTION_LOG_ID).title(DEFAULT_TITLE).logText(DEFAULT_LOG_TEXT).action(action);
        ActionLogDTO dto = Mappers.getMapper( ActionLogMapper.class ).toDto(actionLog);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(DEFAULT_ACTION_LOG_ID, dto.getId());
        Assertions.assertEquals(DEFAULT_TITLE, dto.getTitle());
        Assertions.assertEquals(DEFAULT_LOG_TEXT, dto.getLogText());
        Assertions.assertEquals(DEFAULT_ACTION_ID, dto.getActionId());
    }
}
