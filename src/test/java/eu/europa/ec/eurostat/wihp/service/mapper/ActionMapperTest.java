package eu.europa.ec.eurostat.wihp.service.mapper;

import eu.europa.ec.eurostat.wihp.domain.Acquisition;
import eu.europa.ec.eurostat.wihp.domain.Action;
import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionAction;
import eu.europa.ec.eurostat.wihp.service.dto.ActionDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ActionMapperTest {

    private static final Long DEFAULT_ACTION_ID = 1l;
    private static final Long DEFAULT_ACQUISITION_ID = 2L;
    private static final boolean DEFAULT_SUCCESS = true;
    private static final AcquisitionAction DEFAULT_ACTION = AcquisitionAction.PAUSE;

    @Test
    public void checkToDto_thenCopyFields() {
        Acquisition acquisition = new Acquisition().id(DEFAULT_ACQUISITION_ID);
        Action action = new Action()
            .id(DEFAULT_ACTION_ID)
            .success(DEFAULT_SUCCESS)
            .action(AcquisitionAction.PAUSE)
            .acquisition(acquisition);
        ActionDTO dto = Mappers.getMapper( ActionMapper.class ).toDto(action);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(DEFAULT_ACTION_ID, dto.getId());
        Assertions.assertNotNull(dto.getDate());
        Assertions.assertEquals(DEFAULT_SUCCESS, dto.getSuccess());
        Assertions.assertEquals(DEFAULT_ACTION, dto.getAction());
        Assertions.assertEquals(DEFAULT_ACQUISITION_ID, dto.getAcquisitionId());
    }
}
