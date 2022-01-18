package org.accounting.system.validators;

import io.netty.util.internal.StringUtil;
import org.accounting.system.constraints.ZuluTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Instant;

/**
 * Validates whether the value has the appropriate Zulu format.
 */
public class ZuluTimeValidator implements ConstraintValidator<ZuluTime, String> {
    @Override
    public void initialize(ZuluTime constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {


        String defaultMessage = context.getDefaultConstraintMessageTemplate();

        StringBuilder builder = new StringBuilder();

        builder.append(defaultMessage);
        builder.append(StringUtil.SPACE);
        builder.append("must be a valid zulu timestamp.");
        builder.append(StringUtil.SPACE);
        builder.append("found: ");
        builder.append(value);

        context.disableDefaultConstraintViolation();
        context
                .buildConstraintViolationWithTemplate(builder.toString())
                .addConstraintViolation();


        if (StringUtil.isNullOrEmpty(value)) {
            return false;
        }

        try{
            Instant.parse(value);
        } catch (Exception e){
            return false;
        }

        return true;
    }
}