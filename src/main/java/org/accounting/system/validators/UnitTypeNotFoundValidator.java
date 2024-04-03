package org.accounting.system.validators;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.StringUtil;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.accounting.system.constraints.UnitTypeNotFound;
import org.accounting.system.exceptions.CustomValidationException;
import org.accounting.system.services.UnitTypeService;
import org.apache.commons.lang3.StringUtils;

/**
 * This {@link UnitTypeNotFoundValidator} defines the logic to validate the {@link UnitTypeNotFound}.
 * Essentially, it checks if the given unit type exists in the database.
 * If not exists, it throws a {@link CustomValidationException} with http status 404.
 */
public class UnitTypeNotFoundValidator implements ConstraintValidator<UnitTypeNotFound, String> {

    private String message;

    @Override
    public void initialize(UnitTypeNotFound constraintAnnotation) {
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

        UnitTypeService service = CDI.current().select(UnitTypeService.class).get();

        service.getUnitByType(value).orElseThrow(()->new CustomValidationException(builder.toString(), HttpResponseStatus.NOT_FOUND));

        return true;
    }
}