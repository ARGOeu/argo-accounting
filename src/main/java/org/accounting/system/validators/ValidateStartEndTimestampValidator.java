package org.accounting.system.validators;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.accounting.system.constraints.ValidateStartEndTimestamp;
import org.accounting.system.dtos.metric.MetricRequestDto;
import org.accounting.system.exceptions.CustomValidationException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Instant;
import java.util.Objects;

/**
 * First, this validator checks if the time_period_start and time_period_end are empty.
 * Second, it validates whether the time_period_start and time_period_end have the appropriate Zulu format.
 * Third, it examines whether the time_period_start is after the time_period_end.
 * Finally, it checks if those attributes are equal to each other.
 * In all cases, a {@link CustomValidationException} is thrown enhanced with the corresponding message.
 */
public class ValidateStartEndTimestampValidator implements ConstraintValidator<ValidateStartEndTimestamp, MetricRequestDto> {

    @Override
    public void initialize(ValidateStartEndTimestamp constraintAnnotation) {
    }

    @Override
    public boolean isValid(MetricRequestDto request, ConstraintValidatorContext constraintValidatorContext) {

        if(Objects.isNull(request.start)){
            throw new CustomValidationException("time_period_start may not be empty.", HttpResponseStatus.BAD_REQUEST);
        }

        if(Objects.isNull(request.end)){
            throw new CustomValidationException("time_period_end may not be empty.", HttpResponseStatus.BAD_REQUEST);
        }

        try{
            Instant.parse(request.start);
        } catch (Exception e){
            throw new CustomValidationException("time_period_start must be a valid zulu timestamp. found: "+request.start, HttpResponseStatus.BAD_REQUEST);
        }

        try{
            Instant.parse(request.end);
        } catch (Exception e){
            throw new CustomValidationException("time_period_end must be a valid zulu timestamp. found: "+request.end, HttpResponseStatus.BAD_REQUEST);
        }

        if(Instant.parse(request.start).isAfter(Instant.parse(request.end))){
            throw new CustomValidationException("Timestamp of the starting date time cannot be after of Timestamp of the end date time.", HttpResponseStatus.BAD_REQUEST);
        } else if(Instant.parse(request.start).equals(Instant.parse(request.end))){
            throw new CustomValidationException("Timestamp of the starting date time cannot be equal to Timestamp of the end date time.", HttpResponseStatus.BAD_REQUEST);
        }

        return true;
    }
}