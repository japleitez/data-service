package eu.europa.ec.eurostat.wihp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import eu.europa.ec.eurostat.wihp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ActionLogDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ActionLogDTO.class);
        ActionLogDTO actionLogDTO1 = new ActionLogDTO();
        actionLogDTO1.setId(1L);
        ActionLogDTO actionLogDTO2 = new ActionLogDTO();
        assertThat(actionLogDTO1).isNotEqualTo(actionLogDTO2);
        actionLogDTO2.setId(actionLogDTO1.getId());
        assertThat(actionLogDTO1).isEqualTo(actionLogDTO2);
        actionLogDTO2.setId(2L);
        assertThat(actionLogDTO1).isNotEqualTo(actionLogDTO2);
        actionLogDTO1.setId(null);
        assertThat(actionLogDTO1).isNotEqualTo(actionLogDTO2);
    }
}
