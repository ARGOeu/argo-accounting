package org.accounting.system.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.accounting.system.constraints.CheckDateFormat;

import java.time.format.DateTimeFormatter;

public class CheckDateValidator implements ConstraintValidator<CheckDateFormat, String> {

    private String pattern;

    @Override
    public void initialize(CheckDateFormat constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(String object, ConstraintValidatorContext constraintContext) {
        if ( object == null ) {
            return true;
        }

        try {
            var formatter = DateTimeFormatter.ofPattern(pattern);
            formatter.parse(object);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}