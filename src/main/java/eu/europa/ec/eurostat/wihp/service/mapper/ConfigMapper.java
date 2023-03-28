package eu.europa.ec.eurostat.wihp.service.mapper;

import eu.europa.ec.eurostat.wihp.domain.*;
import eu.europa.ec.eurostat.wihp.service.dto.ConfigDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Config} and its DTO {@link ConfigDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ConfigMapper extends EntityMapper<ConfigDTO, Config> {

    @Override
    @Mapping(source = "acquisition.id", target = "acquisitionId")
    ConfigDTO toDto(Config entity);

    @Mapping(target = "id", ignore = true)
    Config toConfig(ConfigDTO dto, Acquisition acquisition) ;
}
