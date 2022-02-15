package org.accounting.system.validators;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.StringUtil;
import io.vavr.control.Try;
import org.accounting.system.constraints.MetricNotFound;
import org.accounting.system.exceptions.CustomValidationException;
import org.accounting.system.services.MetricService;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.inject.spi.CDI;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * This {@link MetricNotFoundValidator} defines the logic to validate the {@link MetricNotFound}.
 * Essentially, it checks if the given Metric exists in the database.
 * If not exists, it throws a {@link CustomValidationException} with http status 404.
 */
public class MetricNotFoundValidator implements ConstraintValidator<MetricNotFound, String> {

    private String message;

    @Override
    public void initialize(MetricNotFound constraintAnnotation) {
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

        MetricService metricService = CDI.current().select(MetricService.class).get();

        Try
                .run(()->metricService.findById(value).orElseThrow(()->new CustomValidationException(builder.toString(), HttpResponseStatus.NOT_FOUND)))
                .getOrElseThrow(()->new CustomValidationException(builder.toString(), HttpResponseStatus.NOT_FOUND));

        return true;
    }
}