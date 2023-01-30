package org.accounting.system.validators;

import org.accounting.system.constraints.StringEnumeration;
import org.accounting.system.util.Utility;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class StringEnumerationValidator implements ConstraintValidator<StringEnumeration, String> {

    private Set<String> AVAILABLE_ENUM_NAMES;
    private String message;

    @Override
    public void initialize(StringEnumeration stringEnumeration) {
        AVAILABLE_ENUM_NAMES = Utility.getNamesSet(stringEnumeration.enumClass());
        message = stringEnumeration.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(!check(value)){
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate("The value "+value+" is not a valid "+message+". Valid "+message+" values are: "+AVAILABLE_ENUM_NAMES)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private boolean check(String value){
        if ( value == null ) {
            return true;
        } else {
            return AVAILABLE_ENUM_NAMES.contains(value);
        }
    }

}
