package eu.europa.ec.eurostat.wihp.service.bulk;

import eu.europa.ec.eurostat.wihp.config.ApplicationProperties;
import eu.europa.ec.eurostat.wihp.repository.SourceRepository;
import eu.europa.ec.eurostat.wihp.service.dto.SourceListDTO;
import eu.europa.ec.eurostat.wihp.service.mapper.SourceMapper;
import eu.europa.ec.eurostat.wihp.util.JsonModelUtils;
import eu.europa.ec.eurostat.wihp.web.rest.model.SourceListValidatorTest;
import eu.europa.ec.eurostat.wihp.web.rest.model.SourcesJsonModelBuilderTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class BulkSourceRepositoryTest {

    @InjectMocks
    private BulkSourceRepository unit;

    @Mock
    private SourceRepository sourceRepository = Mockito.mock(SourceRepository.class);

    @Mock
    private ApplicationProperties applicationProperties = Mockito.mock(ApplicationProperties.class);

    private SourceMapper sourceMapper = Mappers.getMapper(SourceMapper.class);

    @BeforeEach
    public void setUp() {
        unit = new BulkSourceRepository(sourceRepository, sourceMapper, applicationProperties);
    }

    @Test
    void WhenListIsempty_thenListOfNameDoesNotThrow() throws IOException, URISyntaxException {

        SourceListDTO model = new JsonModelUtils()
            .getSourceListJsonModel(SourceListValidatorTest.EMPTY_SOURCE_LIST_JSON);

        List<String> lst = unit.extractListOfUniqueNames(sourceMapper.fromListJsonDtoToListResponseDto(model.getSources()));
        Assertions.assertEquals(0, lst.size());
    }

    @Test
    void WhenListIsNotEmpty_thenListContainsNames() throws IOException, URISyntaxException {

        SourceListDTO model = new JsonModelUtils()
            .getSourceListJsonModel(SourcesJsonModelBuilderTest.SOURCE_LIST_JSON);

        List<String> lst = unit.extractListOfUniqueNames(sourceMapper.fromListJsonDtoToListResponseDto(model.getSources()));
        Assertions.assertTrue(lst.size() > 0);
    }
}
