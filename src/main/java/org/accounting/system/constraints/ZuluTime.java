package org.accounting.system.constraints;

import org.accounting.system.validators.ZuluTimeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
        ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ZuluTimeValidator.class)
@Documented
public @interface ZuluTime {

    String message() default "must be a valid zulu timestamp." +
            " found:";
    boolean acceptEmptyValue() default false;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
