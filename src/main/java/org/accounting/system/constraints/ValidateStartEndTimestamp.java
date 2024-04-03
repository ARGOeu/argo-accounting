package org.accounting.system.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.accounting.system.validators.ValidateStartEndTimestampValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidateStartEndTimestampValidator.class)
@Documented
public @interface ValidateStartEndTimestamp {

    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
