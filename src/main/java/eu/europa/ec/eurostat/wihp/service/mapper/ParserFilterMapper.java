package eu.europa.ec.eurostat.wihp.service.mapper;

import eu.europa.ec.eurostat.wihp.domain.ParserFilter;
import eu.europa.ec.eurostat.wihp.service.dto.ParserFilterDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring" )
public interface ParserFilterMapper extends EntityMapper<ParserFilterDTO, ParserFilter>  {

    @Mappings({@Mapping(target = "id", ignore = true),
        @Mapping(target = "crawler", ignore = true)
    })
    void filterDtoToFilter(ParserFilterDTO parserFilterDTO, @MappingTarget ParserFilter parserFilter);

}
