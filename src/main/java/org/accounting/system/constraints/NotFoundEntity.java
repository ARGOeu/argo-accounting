package org.accounting.system.constraints;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.accounting.system.validators.NotFoundEntityValidator;
import org.bson.types.ObjectId;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotFoundEntityValidator.class)
@Documented
public @interface NotFoundEntity{

    String message() default "Not founded:";
    Class<?>[] groups() default {};
    Class<?> id() default ObjectId.class;
    Class<? extends Payload>[] payload() default {};
    Class<? extends PanacheMongoRepositoryBase<?,?>> repository();
}
