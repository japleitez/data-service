package eu.europa.ec.eurostat.wihp.service.playground.model;

import java.util.List;

public class ValidationFilterResult {

    private List<ValidationFilterResultConfiguration> configurations;

    public List<ValidationFilterResultConfiguration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(List<ValidationFilterResultConfiguration> configurations) {
        this.configurations = configurations;
    }

    public static class ValidationFilterResultConfiguration {
        private String id;
        private List<ValidationError> validationErrors;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<ValidationError> getValidationErrors() {
            return validationErrors;
        }

        public void setValidationErrors(List<ValidationError> validationErrors) {
            this.validationErrors = validationErrors;
        }
    }

    public static class ValidationError {
        private String id;
        private String value;
        private String type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}

