package eu.europa.ec.eurostat.wihp.service.mapper;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.eurostat.wihp.web.rest.errors.UnprocessableEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class MultiPartFileMapper {

    private final Logger log = LoggerFactory.getLogger(MultiPartFileMapper.class);

    private final ObjectMapper objectMapper;

    public MultiPartFileMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T convert(MultipartFile in, Class<T> cls) {
        try {
            return objectMapper.readValue(in.getInputStream(), cls);
        } catch (JsonMappingException mappingException) {
            logAndThrow("Error mapping content, due to data or encoding: " + mappingException.getMessage(), cls, "mappingError");
        } catch (JsonParseException parseException) {
            logAndThrow("The file cannot be parsed: " + parseException.getMessage(), cls, "parsingError");
        } catch (IOException ioe) {
            logAndThrow("The file cannot be read: " + ioe.getMessage(), cls, "IOError");
        } catch (Exception ex) {
            logAndThrow("Error mapping content: " + ex.getMessage(), cls, "Error");
        }
        return null;
    }

    private void logAndThrow(String message, Class<?> cls, String errorCode) {
        log.error(message);
        throw new UnprocessableEntityException(message, cls.getName(), errorCode);
    }
}
