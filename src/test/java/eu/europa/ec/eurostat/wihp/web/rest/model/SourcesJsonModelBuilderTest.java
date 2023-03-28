package eu.europa.ec.eurostat.wihp.web.rest.model;

import eu.europa.ec.eurostat.wihp.service.dto.SourceDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceListDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceResponseListDTO;
import eu.europa.ec.eurostat.wihp.service.dto.SourceResponseDTO;
import eu.europa.ec.eurostat.wihp.util.JsonModelUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;


public class SourcesJsonModelBuilderTest {

    protected static final String SINGLE_SOURCE_LIST_JSON = "/singleSourceList.json";
    public static final String SOURCE_LIST_JSON = "/sourceList.json";


    @Test
    void whenLoadSingleSourceArrayFromFile_thenObjectIsCorrect() throws Exception {

        SourceListDTO model = new JsonModelUtils().getSourceListJsonModel(SINGLE_SOURCE_LIST_JSON);
        List<SourceDTO> sources = model.getSources();
        Assertions.assertNotNull(model);
        Assertions.assertEquals(1, sources.size());
    }

    @Test
    void whenLoadMultipleSourceArrayFromFile_thenObjectIsCorrect() throws Exception {

        SourceListDTO model = new JsonModelUtils().getSourceListJsonModel(SOURCE_LIST_JSON);
        List<SourceDTO> sources = model.getSources();
        Assertions.assertNotNull(model);
        Assertions.assertEquals(3, sources.size());
    }

    @Test
    void whenBuildEmptySingleModel_thenObjectIsCorrect() {
        SourceResponseDTO model = new SourceResponseDTO();
        model.setName("a");
        model.setUrl("http://blabla");
        Assertions.assertNotNull(model.getErrors());
    }

    @Test
    void whenBuildEmptyModel_thenObjectIsCorrect() {
        SourceResponseListDTO model = new SourceResponseListDTO();
        Assertions.assertNotNull(model);
        Assertions.assertNotNull(model.getSources());
        model.getSources().add(new SourceResponseDTO());
        Assertions.assertNotNull(model.getSources().get(0).getErrors());
    }


}
