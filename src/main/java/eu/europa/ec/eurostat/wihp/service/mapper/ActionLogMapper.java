package eu.europa.ec.eurostat.wihp.service.mapper;

import eu.europa.ec.eurostat.wihp.domain.ActionLog;
import eu.europa.ec.eurostat.wihp.service.dto.ActionLogDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActionLogMapper extends EntityMapper<ActionLogDTO, ActionLog> {

    @Mapping(source = "action.id", target = "actionId")
    ActionLogDTO toDto(ActionLog actionLog);

    @Mapping(target = "action", ignore = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "actionId", target = "action.id")
    ActionLog toEntity(ActionLogDTO dto);

}
