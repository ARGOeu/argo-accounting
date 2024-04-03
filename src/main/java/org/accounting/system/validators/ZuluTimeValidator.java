package org.accounting.system.validators;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.StringUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.accounting.system.constraints.ZuluTime;
import org.accounting.system.exceptions.CustomValidationException;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;

/**
 * Validates whether the value has the appropriate Zulu format.
 */
public class ZuluTimeValidator implements ConstraintValidator<ZuluTime, String> {

    private boolean acceptEmptyValue;

    @Override
    public void initialize(ZuluTime constraintAnnotation) {
        this.acceptEmptyValue = constraintAnnotation.acceptEmptyValue();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if(acceptEmptyValue && StringUtils.isEmpty(value)){
            return true;
        }

        String defaultMessage = context.getDefaultConstraintMessageTemplate();

        StringBuilder builder = new StringBuilder();

        builder.append(defaultMessage);
        builder.append(StringUtil.SPACE);
        builder.append("must be a valid zulu timestamp.");
        builder.append(StringUtil.SPACE);
        builder.append("found: ");
        builder.append(value);

        try{
            Instant.parse(value);
        } catch (Exception e){
            throw new CustomValidationException(builder.toString(), HttpResponseStatus.BAD_REQUEST);
        }

        return true;
    }
}