package org.accounting.system.constraints;

import org.accounting.system.validators.MetricNotFoundValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MetricNotFoundValidator.class)
@Documented
public @interface MetricNotFound {

    String message() default "There is no Metric with the following id:";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
