package eu.europa.ec.eurostat.wihp.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.eurostat.wihp.domain.*;
import eu.europa.ec.eurostat.wihp.service.dto.CrawlerDTO;
import eu.europa.ec.eurostat.wihp.service.dto.DynamicConfigDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceDTO;
import eu.europa.ec.eurostat.wihp.util.JsonNodeUtils;
import org.mapstruct.*;
import org.mapstruct.control.DeepClone;

import java.util.List;

import static java.util.Objects.isNull;

@Mapper(componentModel = "spring", uses = {ParserFilterMapper.class, UrlFilterMapper.class, ParseFilterMapper.class, SourceMapper.class})
public interface CrawlerMapper extends EntityMapper<CrawlerDTO, Crawler> {

    default JsonNode map(DynamicConfigDTO value) {
        return JsonNodeUtils.createJsonNode(isNull(value) ? new DynamicConfigDTO() : value);
    }

    default DynamicConfigDTO map(JsonNode dynamicConfig) {
        if (isNull(dynamicConfig)) {
            return new DynamicConfigDTO();
        }
        try {
            return JsonNodeUtils.getObject(dynamicConfig, DynamicConfigDTO.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException(" Dynamic Config parameters are wrong :  " + dynamicConfig);
        }
    }

    @Mapping(target = "parserFilters", ignore = true)
    @Mapping(target = "sources", ignore = true)
    @Mapping(target = "acquisitions", ignore = true)
    @Mapping(target = "removeSource", ignore = true)
    @Mapping(source = "dynamicConfig", target = "dynamicConfig")
    void updateCrawlerDtoToCrawler(CrawlerDTO crawlerDTO, @MappingTarget Crawler crawler);

    @Mapping(target = "id", ignore = true)
    ParserFilter clone(ParserFilter parserFilter);

    @Mapping(target = "id", ignore = true)
    UrlFilter clone(UrlFilter urlFilter);

    @Mapping(target = "id", ignore = true)
    ParseFilter clone(ParseFilter parseFilter);

    Source clone(Source source);

    @Mapping(target = "removeSource", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "acquisitions", ignore = true)
    @Mapping(target = "parserFilters", ignore = true)
    @Mapping(target = "urlFilters", mappingControl = DeepClone.class)
    @Mapping(target = "parseFilters", mappingControl = DeepClone.class)
    @Mapping(target = "sources", mappingControl = DeepClone.class)
    @Mapping(target = "name", source = "copyName")
    Crawler copy(Crawler crawler, String copyName);


    @Mapping(target = "sources", source = "inputSources")
    Crawler toEntity (CrawlerDTO crawler, List<SourceDTO> inputSources);

    @Named(value = "toDtoNoSources")
    @Mapping(target = "sources", ignore = true)
    CrawlerDTO toDtoNoSources(Crawler crawler);

    @IterableMapping(qualifiedByName = "toDtoNoSources")
    @Mapping(target = "sources", ignore = true)
    List<CrawlerDTO> toDtoNoSources(List<Crawler> crawlers);

    @Named(value = "toDto")
    CrawlerDTO toDto(Crawler crawler);

    @IterableMapping(qualifiedByName = "toDto")
    List<CrawlerDTO> toDto(List<Crawler> crawlers);

}
