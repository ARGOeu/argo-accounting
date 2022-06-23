package org.accounting.system.constraints;

import org.accounting.system.enums.Collection;
import org.accounting.system.enums.Operation;
import org.accounting.system.validators.AccessProjectValidator;

import javax.enterprise.util.Nonbinding;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AccessProjectValidator.class)
@Documented
public @interface AccessProject {

    String message() default "The authenticated client is not permitted to perform the requested operation.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean checkIfExists() default true;
    @Nonbinding Collection collection() default Collection.MetricDefinition;
    @Nonbinding Operation operation() default Operation.CREATE;
}
