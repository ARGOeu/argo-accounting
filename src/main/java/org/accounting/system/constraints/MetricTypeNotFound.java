package org.accounting.system.constraints;

import org.accounting.system.validators.MetricTypeNotFoundValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MetricTypeNotFoundValidator.class)
@Documented
public @interface MetricTypeNotFound {

    String message() default "There is no metric type :";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
