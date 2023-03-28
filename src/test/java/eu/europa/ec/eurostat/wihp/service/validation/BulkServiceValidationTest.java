package eu.europa.ec.eurostat.wihp.service.validation;

import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.domain.Source;
import eu.europa.ec.eurostat.wihp.service.bulk.BulkSourceRepository;
import eu.europa.ec.eurostat.wihp.service.dto.SourceDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceListDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceResponseDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceResponseListDTO;
import eu.europa.ec.eurostat.wihp.service.mapper.SourceMapper;
import eu.europa.ec.eurostat.wihp.util.JsonModelUtils;
import eu.europa.ec.eurostat.wihp.web.rest.model.SourceListValidatorTest;
import eu.europa.ec.eurostat.wihp.web.rest.model.SourcesJsonModelBuilderTest;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


class BulkServiceValidationTest {

    public static final String SOURCE_LIST_WITH_DUPLICATES = "/sourceListWithDuplicates.json";
    @Mock
    private ApplicationProperties applicationProperties = Mockito.mock(ApplicationProperties.class);

    @Mock
    private BulkSourceRepository bulkSourceRepository = Mockito.mock(BulkSourceRepository.class);

    private SourceMapper sourceMapper = Mappers.getMapper(SourceMapper.class);

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    BulkServiceValidation unit = new BulkServiceValidation(factory.getValidator(), bulkSourceRepository);

    @Test
    public void whenNoGroupViolations_thenEmptyListOfError() throws IOException, URISyntaxException {

        SourceListDTO model = new JsonModelUtils()
            .getSourceListJsonModel(SourcesJsonModelBuilderTest.SOURCE_LIST_JSON);

        Assertions.assertTrue(unit.isListObjectValid(model));
    }


    @Test
    void whenLoadDuplicateSourcesList_thenCleanAndDirtyListIsReturned() throws Exception {

        SourceListDTO model = new JsonModelUtils().getSourceListJsonModel(SOURCE_LIST_WITH_DUPLICATES);

        ValidationResult<SourceResponseDTO> result = unit
            .getPartitionsWithDuplicates(sourceMapper.fromListJsonDtoToListResponseDto(model.getSources()));

        Assertions.assertEquals(2, result.getInvalidList().size());
        Assertions.assertEquals(1, result.getValidList().size());
    }


    @Test
    void whenLoadDuplicateSourcesList_thenDuplicateListContainsErrorMessage() throws Exception {

        SourceListDTO model = new JsonModelUtils().getSourceListJsonModel(SOURCE_LIST_WITH_DUPLICATES);

        ValidationResult<SourceResponseDTO> result =
            unit.getPartitionsWithDuplicates(sourceMapper.fromListJsonDtoToListResponseDto(model.getSources()));

        Assertions.assertEquals(BulkServiceValidation.SOURCE_NAME_UNIQUE_VIOLATION_MESSAGE, result.getInvalidList().get(0).getErrors().get(0));
    }


    @Test
    void whenSourceListContainDuplicates_thenTheDuplicatedListContainsAllTheRepetitions() throws Exception {

        SourceResponseDTO redundant = new SourceResponseDTO().setName("b");

        List<SourceResponseDTO> duplicatedSet = new ArrayList<>();
        duplicatedSet.add(redundant);
        duplicatedSet.add(redundant);
        duplicatedSet.add(redundant);
        duplicatedSet.add(redundant);
        duplicatedSet.add(new SourceResponseDTO().setName("33"));

        ValidationResult<SourceResponseDTO> result = unit.getPartitionsWithDuplicates(duplicatedSet);

        Assertions.assertEquals(4, result.getInvalidList().size());
        Assertions.assertEquals(1, result.getValidList().size());

        Assertions.assertEquals(
            BulkServiceValidation.SOURCE_NAME_UNIQUE_VIOLATION_MESSAGE,
            result.getInvalidList().get(0).getErrors().get(0));
    }

    @Test
    void whenSetHasDuplicates_thenEliminateEveryOccurrence() {

        List<SourceResponseDTO> deduplicatedSet = new ArrayList<>();
        SourceResponseDTO redundant = new SourceResponseDTO().setName("b");

        deduplicatedSet.add(new SourceResponseDTO().setName("a"));
        deduplicatedSet.add(redundant);
        List<SourceResponseDTO> duplicatedSet = new ArrayList<>();
        duplicatedSet.add(redundant);
        duplicatedSet.add(redundant);
        duplicatedSet.add(redundant);
        duplicatedSet.add(redundant);

        Assertions.assertEquals(4, duplicatedSet.size());

        List<SourceResponseDTO> out = unit.getCleanDoingDeduplicatedMinusDuplicates(deduplicatedSet, duplicatedSet);

        Assertions.assertEquals(1, out.size());
        Assertions.assertFalse(out.contains(redundant));
    }

    @Test
    public void whenNoViolations_thenEmptyListOfError() throws IOException, URISyntaxException {

        SourceListDTO model = new JsonModelUtils()
            .getSourceListJsonModel(SourcesJsonModelBuilderTest.SOURCE_LIST_JSON);

        List<SourceResponseDTO> list = model.getSources()
            .stream().map(m -> unit.validateSource(m)).filter(m -> !m.getErrors().isEmpty()).collect(Collectors.toList());

        Assertions.assertEquals(0, list.size());
    }


    @Test
    public void whenSomeViolation_thenNonEmptyListOfError() throws IOException, URISyntaxException {

        SourceListDTO model = new JsonModelUtils()
            .getSourceListJsonModel(SourceListValidatorTest.NON_VALID_SOURCE_LIST_JSON);

        List<SourceResponseDTO> list = model.getSources()
            .stream().map(m -> unit.validateSource(m)).filter(m -> m.getErrors().isEmpty()).collect(Collectors.toList());

        Assertions.assertTrue(list.size() > 0);

    }
    @Test
    void WhenListOfRedundantPlusListViolationHasElements_thenListContainsBoth() throws IOException, URISyntaxException {

        SourceResponseDTO wErr1 = new SourceResponseDTO().setName("name1").setUrl("http://stuff");
        SourceResponseDTO wErr2 = new SourceResponseDTO().setName("name2").setUrl("http://stuff");
        SourceResponseDTO wRed1 = new SourceResponseDTO().setName("name3").setUrl("http://stuff2");
        SourceResponseDTO wRed2 = new SourceResponseDTO().setName("name4").setUrl("http://stuff2");

        List<SourceResponseDTO> withErrors = Lists.newArrayList(wErr1, wErr2);
        List<SourceResponseDTO> redundant = Lists.newArrayList(wRed1, wRed2);

        List<SourceResponseDTO> withDuplicates = Lists.newArrayList(wErr1, wErr2);

        SourceResponseListDTO result = unit.mergeValidationErrors(withErrors, redundant, withDuplicates);

        Assertions.assertEquals(6, result.getSources().size());

        Assertions.assertTrue(result.getSources().stream().anyMatch(s -> s.getName().contains("name1")));
        Assertions.assertTrue(result.getSources().stream().anyMatch(s -> s.getName().contains("name2")));
        Assertions.assertTrue(result.getSources().stream().anyMatch(s -> s.getName().contains("name3")));
        Assertions.assertTrue(result.getSources().stream().anyMatch(s -> s.getName().contains("name4")));
    }

    @Test
    void WhenListOfRedundantPlusListViolationAreEmpty_thenListReturnEmptyOptional() throws IOException, URISyntaxException {

        SourceResponseListDTO result = unit.mergeValidationErrors(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        Assertions.assertTrue(result.getSources().isEmpty());
    }

    private boolean redundantIsThere(List<SourceResponseDTO> result, String name) {
        return result
            .stream()
            .filter(s -> s.getName().contains(name))
            .findFirst()
            .get().getErrors().contains(unit.SOURCE_REDUNDANT);
    }

    @Test
    void WhenListOfSourcesContainsName_thenThenReturnTrue() {

        final String THE_NAME = "aName";
        List<Source> sources = new ArrayList<Source>();
        sources.add(new Source().name(THE_NAME));

        SourceResponseDTO model = new SourceResponseDTO();
        model.setName(THE_NAME);

        Assertions.assertTrue(unit.contains(sources, model));
    }

    @Test
    public void whenAllRedundant_thenRedundantPartitionOnly() throws IOException, URISyntaxException {

        SourceListDTO model = new JsonModelUtils()
            .getSourceListJsonModel(SourcesJsonModelBuilderTest.SOURCE_LIST_JSON);

        List<SourceDTO> inlist = model.getSources();

        List<Source> redundant = sourceMapper.fromJsonListDtoToEntities(inlist);
        Mockito.when(applicationProperties.getMaxSourcesBulkSize()).thenReturn(999);
        Mockito.when(bulkSourceRepository.getRedundantFromDb(sourceMapper.fromListJsonDtoToListResponseDto(model.getSources()))).thenReturn(redundant);

        ValidationResult<SourceResponseDTO> partition = unit.getPartitionsWithRedundant(sourceMapper.fromListJsonDtoToListResponseDto(model.getSources()));
        Assertions.assertEquals(0, partition.getValidList().size());
        Assertions.assertEquals(3, partition.getInvalidList().size());

        Assertions.assertTrue(redundantIsThere(partition.getInvalidList(), "source1"));
        Assertions.assertTrue(redundantIsThere(partition.getInvalidList(), "source2"));
        Assertions.assertTrue(redundantIsThere(partition.getInvalidList(), "source3"));
    }

    @Test
    public void whenSomeRedundant_thenAddedToErrorList() throws IOException, URISyntaxException {

        SourceListDTO model = new JsonModelUtils()
            .getSourceListJsonModel(SourcesJsonModelBuilderTest.SOURCE_LIST_JSON);

        List<SourceDTO> inlist = model.getSources();

        List<Source> redundant = sourceMapper.fromJsonListDtoToEntities(inlist);

        redundant.remove(0);
        Mockito.when(applicationProperties.getMaxSourcesBulkSize()).thenReturn(999);
        Mockito.when(bulkSourceRepository.getRedundantFromDb(sourceMapper.fromListJsonDtoToListResponseDto(model.getSources()))).thenReturn(redundant);

        ValidationResult<SourceResponseDTO> partition = unit.getPartitionsWithRedundant(sourceMapper.fromListJsonDtoToListResponseDto(model.getSources()));

        Assertions.assertEquals(1, partition.getValidList().size());
        Assertions.assertEquals(2, partition.getInvalidList().size());
    }

    @Test
    public void whenSomeViolation_thenPartitionListOfSources() throws IOException, URISyntaxException {

        SourceListDTO model = new JsonModelUtils()
            .getSourceListJsonModel(SourceListValidatorTest.NON_VALID_SOURCE_LIST_JSON);

        ValidationResult<SourceResponseDTO> violationValidation = unit.getPartitionsWithViolations(model.getSources());
        Assertions.assertEquals(2, violationValidation.getInvalidList().size());
        Assertions.assertEquals(1, violationValidation.getValidList().size());
    }

    @Test
    public void whenSomeViolations_thenValidListSmaller() throws IOException, URISyntaxException {

        SourceListDTO model = new JsonModelUtils()
            .getSourceListJsonModel(SourceListValidatorTest.NON_VALID_SOURCE_LIST_JSON);

        ValidationResult<SourceResponseDTO> violationValidation = unit.getPartitionsWithViolations(model.getSources());

        List<SourceResponseDTO> cleanList = violationValidation.getValidList();
        Assertions.assertTrue(model.getSources().size() > cleanList.size());

    }

}
