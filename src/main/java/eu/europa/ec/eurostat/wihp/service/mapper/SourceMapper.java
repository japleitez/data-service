package eu.europa.ec.eurostat.wihp.service.mapper;

import eu.europa.ec.eurostat.wihp.domain.Source;
import eu.europa.ec.eurostat.wihp.service.dto.SourceDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring" )
public interface SourceMapper extends EntityMapper<SourceDTO, Source>   {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "crawlers", ignore = true),
        @Mapping(target = "name", source = "name"),
        @Mapping(target = "url", source = "url")
    })
    List<Source> fromJsonListDtoToEntities(List<SourceDTO> source);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "crawlers", ignore = true),
        @Mapping(target = "name", source = "name"),
        @Mapping(target = "url", source = "url")
    })
    List<Source> sourceResponseListToEntities(List<SourceResponseDTO> source);


    @Mappings({
        @Mapping(target = "name", source = "name"),
        @Mapping(target = "url", source = "url")
    })
    List<SourceResponseDTO> fromListJsonDtoToListResponseDto(List<SourceDTO> source);

    @Mappings({
        @Mapping(target = "name", source = "name"),
        @Mapping(target = "url", source = "url")
    })
    List<SourceDTO> fromResponseDtoListToSourceDtoList(List<SourceResponseDTO> source);
}
