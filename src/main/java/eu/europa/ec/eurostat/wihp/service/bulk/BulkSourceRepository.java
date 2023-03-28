package eu.europa.ec.eurostat.wihp.service.bulk;

import com.google.common.collect.Lists;
import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.domain.Source;
import eu.europa.ec.eurostat.wihp.repository.SourceRepository;
import eu.europa.ec.eurostat.wihp.service.dto.SourceResponseDTO;
import eu.europa.ec.eurostat.wihp.service.mapper.SourceMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class BulkSourceRepository {

    private final SourceRepository sourceRepository;
    private final SourceMapper sourceMapper;
    private final ApplicationProperties applicationProperties;

    public BulkSourceRepository(SourceRepository sourceRepository, SourceMapper sourceMapper, ApplicationProperties applicationProperties) {
        this.sourceRepository = sourceRepository;
        this.sourceMapper = sourceMapper;
        this.applicationProperties = applicationProperties;
    }

    public List<Source> saveSources(List<SourceResponseDTO> sourceResponses) {
        return sourceRepository.saveAll(sourceMapper.sourceResponseListToEntities(sourceResponses));
    }

    public List<Source> getRedundantFromDb(List<SourceResponseDTO> sourceResponseList) {
        List<String> names = extractListOfUniqueNames(sourceResponseList);
        return Lists.partition(names, applicationProperties.getMaxSourcesBulkSize()).stream()
            .map(sourceRepository::findSourceByNameIn)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    protected List<String> extractListOfUniqueNames(List<SourceResponseDTO> sourceResponseList) {
        return sourceResponseList
            .stream()
            .map(SourceResponseDTO::getName)
            .collect(Collectors.toList());
    }
}
