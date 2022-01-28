package org.accounting.system.exceptionmappers;

import io.quarkus.hibernate.validator.runtime.jaxrs.ResteasyViolationExceptionImpl;
import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.exceptions.CustomValidationException;

import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {
    @Override
    public Response toResponse(ValidationException e) {

        if(e.getCause() instanceof CustomValidationException){

            CustomValidationException exception = (CustomValidationException) e.getCause();
            InformativeResponse response = new InformativeResponse();
            response.message = exception.getMessage();
            response.code = exception.getStatus().code();
            return Response.status(exception.getStatus().code()).entity(response).build();
        } else if(e instanceof ResteasyViolationExceptionImpl){

            InformativeResponse response = new InformativeResponse();
            response.message = ((ResteasyViolationExceptionImpl) e).getConstraintViolations().stream().findFirst().get().getMessageTemplate();
            response.code = Response.Status.BAD_REQUEST.getStatusCode();
            return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
        } else {

            InformativeResponse response = new InformativeResponse();
            response.message = e.getMessage();
            response.code = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
        }
    }
}
