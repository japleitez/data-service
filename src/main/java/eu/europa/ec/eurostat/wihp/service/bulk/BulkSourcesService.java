package eu.europa.ec.eurostat.wihp.service.bulk;

import eu.europa.ec.eurostat.wihp.service.dto.SourceDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceListDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceResponseDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceResponseListDTO;
import eu.europa.ec.eurostat.wihp.service.mapper.MultiPartFileMapper;
import eu.europa.ec.eurostat.wihp.service.mapper.SourceMapper;
import eu.europa.ec.eurostat.wihp.service.validation.BulkServiceValidation;
import eu.europa.ec.eurostat.wihp.service.validation.ValidationResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BulkSourcesService {

    private final BulkServiceValidation bulkServiceValidation;
    private final BulkSourceRepository bulkSourceRepository;
    private final SourceMapper sourceMapper;
    private final MultiPartFileMapper multiPartFileMapper;

    public BulkSourcesService(final BulkServiceValidation bulkServiceValidation,
                              final BulkSourceRepository bulkSourceDao,
                              final SourceMapper sourceMapper,
                              final MultiPartFileMapper multiPartFileMapper) {
        this.bulkServiceValidation = bulkServiceValidation;
        this.bulkSourceRepository = bulkSourceDao;
        this.sourceMapper = sourceMapper;
        this.multiPartFileMapper = multiPartFileMapper;
    }

    public SourceResponseListDTO batchImportSources(final SourceListDTO inModel) {

        ValidationResult<SourceResponseDTO> violationValidation = bulkServiceValidation.getPartitionsWithViolations(inModel.getSources());
        ValidationResult<SourceResponseDTO> duplicationValidation = bulkServiceValidation.getPartitionsWithDuplicates(violationValidation.getValidList());
        ValidationResult<SourceResponseDTO> redundantValidation = bulkServiceValidation.getPartitionsWithRedundant(duplicationValidation.getValidList());

        bulkSourceRepository.saveSources(redundantValidation.getValidList());

        return mergeValidationErrors(violationValidation.getInvalidList(),redundantValidation.getInvalidList(),duplicationValidation.getInvalidList());
    }

    public SourceResponseListDTO mergeValidationErrors(List <SourceResponseDTO> violations, List <SourceResponseDTO> redundant,List <SourceResponseDTO>  duplication){
        return bulkServiceValidation
            .mergeValidationErrors(
                violations,
                redundant,
                duplication);
    }

    public boolean isObjectValid(final SourceListDTO inModel) {
        return bulkServiceValidation.isListObjectValid(inModel);
    }

    public List<SourceDTO> saveSources(List<SourceResponseDTO> sourceResponses) {
        return bulkSourceRepository.saveSources(sourceResponses).stream().map(sourceMapper::toDto).collect(Collectors.toList());
    }

    public SourceListDTO convertFile(final MultipartFile file)  {
        return multiPartFileMapper.convert(file, SourceListDTO.class);
    }
}

