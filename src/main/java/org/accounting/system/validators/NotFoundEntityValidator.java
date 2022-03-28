package org.accounting.system.validators;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.StringUtil;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.vavr.control.Try;
import org.accounting.system.constraints.NotFoundEntity;
import org.accounting.system.exceptions.CustomValidationException;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import javax.enterprise.inject.spi.CDI;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * This {@link NotFoundEntityValidator} defines the logic to validate the {@link NotFoundEntity}.
 * Essentially, it checks if the given Entity exists in the database.
 * If not exists, it throws a {@link CustomValidationException} with http status 404.
 */
public class NotFoundEntityValidator implements ConstraintValidator<NotFoundEntity, String> {

    private String message;
    private Class<? extends PanacheMongoRepository> repository;

    @Override
    public void initialize(NotFoundEntity constraintAnnotation) {
        this.message = constraintAnnotation.message();
        this.repository = constraintAnnotation.repository();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if(StringUtils.isEmpty(value)){
            return true;
        }

        StringBuilder builder = new StringBuilder();

        builder.append(message);
        builder.append(StringUtil.SPACE);
        builder.append(value);

        PanacheMongoRepository repository = CDI.current().select(this.repository).get();

        Try
                .run(()->repository.findByIdOptional(new ObjectId(value)).orElseThrow(()->new CustomValidationException(builder.toString(), HttpResponseStatus.NOT_FOUND)))
                .getOrElseThrow(()->new CustomValidationException(builder.toString(), HttpResponseStatus.NOT_FOUND));

        return true;
    }
}