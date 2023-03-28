package eu.europa.ec.eurostat.wihp.service.mapper;

import eu.europa.ec.eurostat.wihp.domain.Report;
import eu.europa.ec.eurostat.wihp.service.dto.ReportDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Mapper for the entity {@link Report} and its DTO {@link ReportDTO}.
 */
@Mapper(componentModel = "spring", uses = { AcquisitionMapper.class })
public interface ReportMapper extends EntityMapper<ReportDTO, Report> {
    @Mappings({ @Mapping(target = "acquisitionId", source = "acquisition.id") })
    ReportDTO toDto(Report s);
}
