package eu.europa.ec.eurostat.wihp.service.validation;

import eu.europa.ec.eurostat.wihp.domain.Source;
import eu.europa.ec.eurostat.wihp.service.bulk.BulkSourceRepository;
import eu.europa.ec.eurostat.wihp.service.dto.SourceDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceListDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceResponseDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceResponseListDTO;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BulkServiceValidation {

    private final Validator validator;
    protected static final String SOURCE_NAME_UNIQUE_VIOLATION_MESSAGE = "The source name must be unique";
    protected static final String SOURCE_REDUNDANT = "This source is redundant";

    private final BulkSourceRepository bulkSourceRepository;


    public BulkServiceValidation(Validator validator, BulkSourceRepository bulkSourceRepository) {
        this.validator = validator;
        this.bulkSourceRepository = bulkSourceRepository;
    }

    public boolean isListObjectValid(SourceListDTO m) {
        return validator.validate(m).isEmpty();
    }

    public SourceResponseDTO validateSource(final SourceDTO model) {
        return new SourceResponseDTO(model, validator.validate(model).stream().map(ConstraintViolation::getMessage).collect(Collectors.toList()));
    }

    public ValidationResult<SourceResponseDTO> getPartitionsWithDuplicates(final List<SourceResponseDTO> inputSources) {

        ValidationResult<SourceResponseDTO> duplicationValidation = identifyDuplicates(inputSources);

        List<SourceResponseDTO> cleanList = getCleanDoingDeduplicatedMinusDuplicates(duplicationValidation.getValidList(), duplicationValidation.getInvalidList());
        List<SourceResponseDTO> errorList = getErrorsDoingInitialMinusClean(cleanList, inputSources);

        return new ValidationResult<>(errorList,cleanList);
    }

    private ValidationResult<SourceResponseDTO> identifyDuplicates(final List<SourceResponseDTO> intSources) {
        Set<SourceResponseDTO> deduplicatedSet = new HashSet<>();
        List<SourceResponseDTO> listWithDuplicates = intSources
            .stream()
            .filter(n -> !deduplicatedSet.add(n))
            .collect(Collectors.toList());

        return new ValidationResult<>(listWithDuplicates,new ArrayList<>(deduplicatedSet));
    }

    protected List<SourceResponseDTO> getErrorsDoingInitialMinusClean(final List<SourceResponseDTO> cleanList, final List<SourceResponseDTO> initialLIst) {
        List<SourceResponseDTO> errorsOutput = new ArrayList<>(initialLIst);
        errorsOutput.removeAll(cleanList);
        return errorsOutput.stream()
            .filter(s -> s.getErrors().add(SOURCE_NAME_UNIQUE_VIOLATION_MESSAGE))
            .collect(Collectors.toList());
    }

    protected List<SourceResponseDTO> getCleanDoingDeduplicatedMinusDuplicates(final List<SourceResponseDTO> deduplicatedSet, final List<SourceResponseDTO> duplicatedSet) {
        List<SourceResponseDTO> cloneOutput = new ArrayList<>(deduplicatedSet);
        cloneOutput.removeAll(duplicatedSet);
        return cloneOutput;
    }

    public ValidationResult<SourceResponseDTO> getPartitionsWithViolations(final List<SourceDTO> sources) {
        Map<Boolean, List<SourceResponseDTO>> trueWhenViolations = sources
            .stream()
            .map(this::validateSource)
            .collect(Collectors.partitioningBy(out -> !out.getErrors().isEmpty()));

        return new ValidationResult<>(trueWhenViolations.get(true),trueWhenViolations.get(false));
    }

    public SourceResponseListDTO mergeValidationErrors(final List<SourceResponseDTO> withViolations,
                                                       final List<SourceResponseDTO> redundant,
                                                       final List<SourceResponseDTO> duplicates) {

        List<SourceResponseDTO> withIssues = new ArrayList<>();
        withIssues.addAll(redundant);
        withIssues.addAll(withViolations);
        withIssues.addAll(duplicates);

        return new SourceResponseListDTO().setSources(withIssues);
    }

    public ValidationResult<SourceResponseDTO> getPartitionsWithRedundant(List<SourceResponseDTO> sourceResponseList) {
        List<Source> redundant = bulkSourceRepository.getRedundantFromDb(sourceResponseList);
        Map<Boolean, List<SourceResponseDTO>> result = sourceResponseList.stream().collect(Collectors.partitioningBy(s -> contains(redundant, s)));
        result.get(true).forEach(e -> e.getErrors().add(SOURCE_REDUNDANT));
        return new ValidationResult<>(result.get(true),result.get(false));
    }

    public ValidationResult<SourceResponseDTO> getRedundantWithId(List<SourceResponseDTO> sourceResponseList) {
        List<Source> redundant = bulkSourceRepository.getRedundantFromDb(sourceResponseList);
        Map<Boolean, List<SourceResponseDTO>> result = sourceResponseList.stream().collect(Collectors.partitioningBy(s -> contains(redundant, s)));
        addIdToSources(result.get(true),  redundant);
        return  new ValidationResult<>(result.get(true),result.get(false));
    }

    private void addIdToSources(List<SourceResponseDTO> sources, List<Source> redundant  ){
        Source sourceNullId = new Source();
        Map<String, Source> redundantByName = redundant.stream().collect(Collectors.toMap(Source::getName, Function.identity()));
        sources.forEach(s -> s.setId(redundantByName.getOrDefault(s.getName(), sourceNullId).getId()));
    }

    protected boolean contains(List<Source> redundant, SourceResponseDTO sourceResponse) {
        return redundant.stream().anyMatch(r -> r.getName().equals(sourceResponse.getName()));
    }
}
