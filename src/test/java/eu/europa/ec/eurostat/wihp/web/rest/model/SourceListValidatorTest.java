package eu.europa.ec.eurostat.wihp.web.rest.model;

import eu.europa.ec.eurostat.wihp.service.dto.SourceListDTO;
import eu.europa.ec.eurostat.wihp.util.JsonModelUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SourceListValidatorTest {

    public static final String NON_VALID_SOURCE_LIST_JSON = "/sourceListNonValid.json";
    public static final String EMPTY_SOURCE_LIST_JSON = "/sourceListEmpty.json";
    public static final String VALID_SOURCE_LIST_JSON = "/sourceList.json";

    @Test
    void whenTheSourceListIsNotValid_ThenGetNonEmptyViolationList() throws IOException, URISyntaxException {

        SourceListDTO model = new JsonModelUtils()
            .getSourceListJsonModel(NON_VALID_SOURCE_LIST_JSON);

        List<String> viol = JsonModelUtils.getSingleViolationMessages(model);

        List<String> l = new ArrayList<String>();
        l.add("The url is not valid");
        l.add("The name has illegal character");

        List<String> err = l.stream()
            .filter(s->viol.stream().anyMatch(r->r.equals(s)))
            .collect(Collectors.toList());

        Assertions.assertTrue(viol.size()>0);
        Assertions.assertEquals(2, err.size());
    }

    @Test
    void whenTheSourceListIsEmpty_ThenGetNonEmptyViolationList() throws IOException, URISyntaxException {

        final String EXPECTED = "'sources' cannot be empty";
        SourceListDTO model = new JsonModelUtils()
            .getSourceListJsonModel(EMPTY_SOURCE_LIST_JSON);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        List<String> viol = JsonModelUtils.getGroupViolationMessages(model);

        Assertions.assertTrue(viol.size()>0);
        Assertions.assertEquals(EXPECTED,viol.get(0));
    }


}
