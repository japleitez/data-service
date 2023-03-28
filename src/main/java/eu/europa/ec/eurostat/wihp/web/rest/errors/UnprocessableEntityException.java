package eu.europa.ec.eurostat.wihp.web.rest.errors;

import eu.europa.ec.eurostat.wihp.service.dto.CrawlerResponseDTO;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class UnprocessableEntityException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    private final String entityName;

    private final String errorKey;

    public UnprocessableEntityException(String defaultMessage, String entityName, String errorKey) {
        this(ErrorConstants.DEFAULT_TYPE, defaultMessage, entityName, errorKey);
    }

    public UnprocessableEntityException(URI type, String defaultMessage, String entityName, String errorKey) {
        super(type, defaultMessage, Status.UNPROCESSABLE_ENTITY, null, null, null, getAlertParameters(entityName, errorKey));
        this.entityName = entityName;
        this.errorKey = errorKey;
    }

    public UnprocessableEntityException(String path, String defaultMessage, String errorKey, CrawlerResponseDTO dto) {
        super(null, defaultMessage, Status.UNPROCESSABLE_ENTITY, null, null, null, getAlertParameters(errorKey, dto, path));
        this.entityName = null;
        this.errorKey = null;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getErrorKey() {
        return errorKey;
    }

    private static Map<String, Object> getAlertParameters(String entityName, String errorKey) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("message", "error." + errorKey);
        parameters.put("params", entityName);
        return parameters;
    }

    private static Map<String, Object> getAlertParameters(String errorKey, CrawlerResponseDTO dto, String type) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("sourceErrors", dto.getSourceErrors());
        parameters.put("FieldsError", dto.getFieldErrors());
        parameters.put("message", "error." + errorKey);
        parameters.put("path", type);
        return parameters;
    }
}
