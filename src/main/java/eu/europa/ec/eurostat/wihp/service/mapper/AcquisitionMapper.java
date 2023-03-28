package eu.europa.ec.eurostat.wihp.service.mapper;

import eu.europa.ec.eurostat.wihp.domain.Acquisition;
import eu.europa.ec.eurostat.wihp.service.dto.AcquisitionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AcquisitionMapper  extends EntityMapper<AcquisitionDTO, Acquisition> {

    @Mapping(target = "crawlerName", source = "crawler.name")
    AcquisitionDTO toDto(Acquisition acquisition);
}
