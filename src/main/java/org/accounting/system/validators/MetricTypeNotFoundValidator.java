package org.accounting.system.validators;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.StringUtil;
import org.accounting.system.constraints.MetricTypeNotFound;
import org.accounting.system.exceptions.CustomValidationException;
import org.accounting.system.services.ReadPredefinedTypesService;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.inject.spi.CDI;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * This {@link MetricTypeNotFoundValidator} defines the logic to validate the {@link MetricTypeNotFound}.
 * Essentially, it checks if the given metric type exists in the database.
 * If not exists, it throws a {@link CustomValidationException} with http status 404.
 */
public class MetricTypeNotFoundValidator implements ConstraintValidator<MetricTypeNotFound, String> {

    private String message;

    @Override
    public void initialize(MetricTypeNotFound constraintAnnotation) {
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

        ReadPredefinedTypesService service = CDI.current().select(ReadPredefinedTypesService.class).get();

        service.searchForMetricType(value).orElseThrow(()->new CustomValidationException(builder.toString(), HttpResponseStatus.NOT_FOUND));

        return true;
    }
}