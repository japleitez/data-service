package eu.europa.ec.eurostat.wihp.service;

import eu.europa.ec.eurostat.wihp.domain.enumeration.AcquisitionAction;
import eu.europa.ec.eurostat.wihp.service.dto.ActionDTO;

public interface AcquisitionActionService {
    ActionDTO execute(final Long acquisitionId, final AcquisitionAction actionToExecute) ;
}
