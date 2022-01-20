package org.accounting.system.exceptionmappers;

import org.accounting.system.dtos.InformativeResponse;
import org.accounting.system.exceptions.UnprocessableException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UnprocessableExceptionMapper implements ExceptionMapper<UnprocessableException> {
    @Override
    public Response toResponse(UnprocessableException e) {

        InformativeResponse response = new InformativeResponse();
        response.message = e.getMessage();
        response.code = 422;
        return Response.status(422).entity(response).build();
    }
}
