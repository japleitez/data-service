package eu.europa.ec.eurostat.wihp.service.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = ParserFilterDTOValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ParserFilterParams {
    String message() default "parserFilter.params.invalid";

    // group of constraints
    Class<?>[] groups() default {};

    // additional information about annotation
    Class<? extends Payload>[] payload() default {};
}
