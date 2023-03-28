package eu.europa.ec.eurostat.wihp.service.mapper;

import eu.europa.ec.eurostat.wihp.domain.ParseFilter;
import eu.europa.ec.eurostat.wihp.service.dto.ParseFilterDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ParseFilterMapper extends EntityMapper<ParseFilterDTO, ParseFilter> {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "crawler", ignore = true)
    void parseFilterDtoToParseFilter(ParseFilterDTO parseFilterDTO, @MappingTarget ParseFilter parseFilter);
}
