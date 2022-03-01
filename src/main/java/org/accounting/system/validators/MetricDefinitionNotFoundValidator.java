package org.accounting.system.validators;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.StringUtil;
import io.vavr.control.Try;
import org.accounting.system.constraints.MetricDefinitionNotFound;
import org.accounting.system.exceptions.CustomValidationException;
import org.accounting.system.repositories.MetricDefinitionRepository;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import javax.enterprise.inject.spi.CDI;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * This {@link MetricDefinitionNotFoundValidator} defines the logic to validate the {@link MetricDefinitionNotFound}.
 * Essentially, it checks if the given Metric Definition exists in the database.
 * If not exists, it throws a {@link CustomValidationException} with http status 404.
 */
public class MetricDefinitionNotFoundValidator implements ConstraintValidator<MetricDefinitionNotFound, String> {

    private String message;

    @Override
    public void initialize(MetricDefinitionNotFound constraintAnnotation) {
        this.message = constraintAnnotation.message();
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

        MetricDefinitionRepository metricDefinitionRepository = CDI.current().select(MetricDefinitionRepository.class).get();

        Try
                .run(()->metricDefinitionRepository.findByIdOptional(new ObjectId(value)).orElseThrow(()->new CustomValidationException(builder.toString(), HttpResponseStatus.NOT_FOUND)))
                .getOrElseThrow(()->new CustomValidationException(builder.toString(), HttpResponseStatus.NOT_FOUND));

        return true;
    }
}