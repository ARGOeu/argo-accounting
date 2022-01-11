package org.accounting.system.exceptionmappers;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.accounting.system.dtos.InformativeResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidFormatExceptionMapper implements ExceptionMapper<InvalidFormatException> {
    @Override
    public Response toResponse(InvalidFormatException e) {
        InformativeResponse response = new InformativeResponse();
        response.message = e.getCause().getMessage();
        response.code = Response.Status.BAD_REQUEST.getStatusCode();
        return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
    }
}
