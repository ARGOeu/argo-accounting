package org.accounting.system.exceptionmappers;

import org.accounting.system.dtos.InformativeResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GeneralExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {

        InformativeResponse response = new InformativeResponse();
        response.message = e.getMessage();
        response.code = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
    }
}
