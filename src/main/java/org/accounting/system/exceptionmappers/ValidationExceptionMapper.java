package org.accounting.system.exceptionmappers;

import io.quarkus.hibernate.validator.runtime.jaxrs.ResteasyViolationExceptionImpl;
import org.accounting.system.dtos.InformativeResponse;

import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {
    @Override
    public Response toResponse(ValidationException e) {

        InformativeResponse response = new InformativeResponse();
        response.message = ((ResteasyViolationExceptionImpl) e).getConstraintViolations().stream().findFirst().get().getMessageTemplate();
        response.code = Response.Status.BAD_REQUEST.getStatusCode();
        return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
    }
}
