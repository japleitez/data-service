package eu.europa.ec.eurostat.wihp.service.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import eu.europa.ec.eurostat.wihp.service.dto.ParserFilterDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.StreamUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;
import java.util.Set;

public class ParserFilterDTOValidator implements ConstraintValidator<ParserFilterParams, Set<ParserFilterDTO>> {

    private static final String REGEX_URL_FILTER = "RegexURLFilter";

    @Override
    public boolean isValid(Set<ParserFilterDTO> parserFilterDTOs, ConstraintValidatorContext ctx) {
        Optional<ParserFilterDTO> optionalPF = findParserFilterDTOForName(parserFilterDTOs, REGEX_URL_FILTER);
        if (optionalPF.isPresent() && !isRegexURLFilterValid(optionalPF.get())) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate(REGEX_URL_FILTER + " is not valid").addConstraintViolation();
            return false;
        }
        return true;
    }

    protected boolean isRegexURLFilterValid(ParserFilterDTO parserFilter) {
        ArrayNode urlFilters = (ArrayNode) parserFilter.getParams().get("urlFilters");
        return StreamUtils
            .createStreamFromIterator(urlFilters.elements())
            .map(JsonNode::asText)
            .noneMatch(this::isUrlFilterExpressionValid);
    }

    protected boolean isUrlFilterExpressionValid(String expression) {
        return (StringUtils.isBlank(expression) || "null".equals(expression));
    }

    protected Optional<ParserFilterDTO> findParserFilterDTOForName(Set<ParserFilterDTO> parserFilters, String name) {
        return parserFilters.stream().filter(item -> item.getName().equals(name)).findFirst();
    }
}
