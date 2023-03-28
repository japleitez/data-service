package eu.europa.ec.eurostat.wihp.service.bulk;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.service.dto.SourceListDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceResponseDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceResponseListDTO;
import eu.europa.ec.eurostat.wihp.service.mapper.MultiPartFileMapper;
import eu.europa.ec.eurostat.wihp.service.mapper.SourceMapper;
import eu.europa.ec.eurostat.wihp.service.validation.BulkServiceValidation;
import eu.europa.ec.eurostat.wihp.service.validation.ValidationResult;
import eu.europa.ec.eurostat.wihp.util.JsonModelUtils;
import eu.europa.ec.eurostat.wihp.web.rest.model.SourceListValidatorTest;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;

class BulkSourcesServiceTest {

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    @Spy
    BulkSourcesService unit;

    @Mock
    private BulkSourceRepository bulkSourceRepository = Mockito.mock(BulkSourceRepository.class);

    BulkServiceValidation bulkServiceValidation = Mockito.mock(BulkServiceValidation.class);

    MultiPartFileMapper multiPartFileMapper = new MultiPartFileMapper(new ObjectMapper());


    @Autowired
    SourceMapper sourceMapper;

    @BeforeEach
    public void setUp() {
        unit = new BulkSourcesService(bulkServiceValidation, bulkSourceRepository, sourceMapper, multiPartFileMapper);
    }


    @Test
    public void whenSomeRedundantAndSomeDuplicate_thenSaveResourcesVerifyOneTime() throws IOException, URISyntaxException {

        SourceListDTO model = new JsonModelUtils()
            .getSourceListJsonModel(SourceListValidatorTest.VALID_SOURCE_LIST_JSON);

        SourceMapper sourceMapper = Mappers.getMapper(SourceMapper.class);

        List<SourceResponseDTO> initialLIst = sourceMapper
            .fromListJsonDtoToListResponseDto(model.getSources());
        ValidationResult<SourceResponseDTO> violationValidation = new ValidationResult<>(new ArrayList<>(),initialLIst);

        Mockito.when(bulkServiceValidation.getPartitionsWithViolations(model.getSources()))
            .thenReturn(violationValidation);

        ValidationResult<SourceResponseDTO> duplicationValidation = new ValidationResult<>(new ArrayList<>(),initialLIst);

        Mockito.when(bulkServiceValidation.getPartitionsWithDuplicates(initialLIst)).thenReturn(duplicationValidation);

        List<SourceResponseDTO> out2 = sourceMapper
            .fromListJsonDtoToListResponseDto(model.getSources());
        List<SourceResponseDTO> redound = Lists.newArrayList(out2.remove(0));
        ValidationResult<SourceResponseDTO> redundantValidation = new ValidationResult<>(out2,redound);

        Mockito.when(bulkServiceValidation.getPartitionsWithRedundant(initialLIst)).thenReturn(redundantValidation);

        Mockito.when(bulkSourceRepository.saveSources(redundantValidation.getValidList())).thenReturn(null);

        Mockito.when(bulkServiceValidation.mergeValidationErrors(violationValidation.getInvalidList(),redundantValidation.getInvalidList(),duplicationValidation.getInvalidList())).thenReturn(new SourceResponseListDTO());

        unit.batchImportSources(model);

        Mockito.verify(bulkSourceRepository, times(1))
            .saveSources( redundantValidation.getValidList() );

        Mockito.verify(bulkServiceValidation, times(1))
            .mergeValidationErrors(violationValidation.getInvalidList(),redundantValidation.getInvalidList(),duplicationValidation.getInvalidList() );

    }
}
