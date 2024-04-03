package org.accounting.system.constraints;

import jakarta.enterprise.util.Nonbinding;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.validators.AccessInstallationValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AccessInstallationValidator.class)
@Documented
public @interface AccessInstallation {

    String message() default "The authenticated client is not permitted to perform the requested operation.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    @Nonbinding Collection collection() default Collection.MetricDefinition;
    @Nonbinding Operation operation() default Operation.CREATE;
}
