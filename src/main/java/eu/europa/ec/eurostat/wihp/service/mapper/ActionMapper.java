package eu.europa.ec.eurostat.wihp.service.mapper;

import eu.europa.ec.eurostat.wihp.domain.Action;
import eu.europa.ec.eurostat.wihp.service.dto.ActionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActionMapper extends EntityMapper<ActionDTO, Action> {

    @Mapping(source = "acquisition.id", target = "acquisitionId")
    ActionDTO toDto(Action action);


    @Mapping(target = "acquisition", ignore = true)
    @Mapping(target = "actionLogs", ignore = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "acquisitionId", target = "acquisition.id")
    Action toEntity(ActionDTO dto);
}
