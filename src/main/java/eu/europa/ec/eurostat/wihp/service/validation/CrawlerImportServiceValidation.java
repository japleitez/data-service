package eu.europa.ec.eurostat.wihp.service.validation;

import eu.europa.ec.eurostat.wihp.service.dto.CrawlerDTO;
import eu.europa.ec.eurostat.wihp.service.dto.CrawlerFieldResponseDTO;
import eu.europa.ec.eurostat.wihp.service.dto.CrawlerResponseDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceResponseDTO;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CrawlerImportServiceValidation {

    public static final String OBJECT_MAPPING = "crawler";
    private final Validator validator;
    private final BulkServiceValidation bulkServiceValidation;

    public CrawlerImportServiceValidation(Validator validator, BulkServiceValidation bulkServiceValidation1) {
        this.validator = validator;
        this.bulkServiceValidation = bulkServiceValidation1;
    }

    public ValidationResult<SourceResponseDTO> validateSources (final CrawlerDTO crawlerDTO) {

        ValidationResult<SourceResponseDTO> violationValidation = bulkServiceValidation.getPartitionsWithViolations(new ArrayList<>(crawlerDTO.getSources()));
        ValidationResult<SourceResponseDTO> duplicationValidation = bulkServiceValidation.getPartitionsWithDuplicates(violationValidation.getValidList());

        ValidationResult<SourceResponseDTO> result = new ValidationResult<>();

        result.getInvalidList().addAll(violationValidation.getInvalidList());
        result.getInvalidList().addAll(duplicationValidation.getInvalidList());
        result.getValidList().addAll(duplicationValidation.getValidList());

        return result;
    }

    public ValidationResult<SourceResponseDTO> splitRedundant(List<SourceResponseDTO> validLIst) {
        return bulkServiceValidation.getRedundantWithId(validLIst);
    }

    public CrawlerResponseDTO validateCrawler(CrawlerDTO crawlerDTO) {
        return new CrawlerResponseDTO(validateFields(crawlerDTO));
    }

    public List<CrawlerFieldResponseDTO> validateFields(CrawlerDTO crawlerDTO) {

        return validator.validate(crawlerDTO)
            .stream()
            .map(this::mapFieldViolation)
            .collect(Collectors.toList());
    }

    private CrawlerFieldResponseDTO mapFieldViolation(ConstraintViolation<CrawlerDTO> violation) {

        return new CrawlerFieldResponseDTO()
            .setMessage(violation.getMessage())
            .setFiled(violation.getPropertyPath()
                .toString()).setObjectName(OBJECT_MAPPING);
    }

}
