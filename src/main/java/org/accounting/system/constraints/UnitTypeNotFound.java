package org.accounting.system.constraints;

import org.accounting.system.validators.UnitTypeNotFoundValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UnitTypeNotFoundValidator.class)
@Documented
public @interface UnitTypeNotFound {

    String message() default "There is no unit type :";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
