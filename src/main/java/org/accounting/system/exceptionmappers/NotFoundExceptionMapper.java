package org.accounting.system.exceptionmappers;

import org.accounting.system.dtos.InformativeResponse;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException e) {

        InformativeResponse response = new InformativeResponse();
        response.message = e.getMessage();
        response.code = e.getResponse().getStatus();
        return Response.status(e.getResponse().getStatus()).entity(response).build();
    }
}
