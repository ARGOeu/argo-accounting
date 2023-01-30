package org.accounting.system.validators;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.StringUtil;
import org.accounting.system.constraints.UnitTypeNotFound;
import org.accounting.system.exceptions.CustomValidationException;
import org.accounting.system.services.ReadPredefinedTypesService;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.inject.spi.CDI;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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

        ReadPredefinedTypesService service = CDI.current().select(ReadPredefinedTypesService.class).get();

        service.searchForUnitType(value).orElseThrow(()->new CustomValidationException(builder.toString(), HttpResponseStatus.NOT_FOUND));

        return true;
    }
}