package eu.europa.ec.eurostat.wihp.service.mapper;

import eu.europa.ec.eurostat.wihp.domain.UrlFilter;
import eu.europa.ec.eurostat.wihp.service.dto.UrlFilterDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UrlFilterMapper extends EntityMapper<UrlFilterDTO, UrlFilter> {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "crawler", ignore = true)
    void urlFilterDtoToUrlFilter(UrlFilterDTO urlFilterDTO, @MappingTarget UrlFilter urlFilter);
}
