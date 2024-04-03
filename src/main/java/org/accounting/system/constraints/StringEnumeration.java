package org.accounting.system.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.accounting.system.validators.StringEnumerationValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = StringEnumerationValidator.class)
@Target({ FIELD, PARAMETER, ElementType.TYPE_USE})
@Retention(RUNTIME)
public @interface StringEnumeration {

    String message() default "{com.xxx.bean.validation.constraints.StringEnumeration.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    Class<? extends Enum<?>> enumClass();
}
