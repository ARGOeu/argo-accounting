package org.accounting.system.exceptionmappers;


import org.accounting.system.dtos.InformativeResponse;

import javax.ws.rs.NotSupportedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotSupportedExceptionMapper implements ExceptionMapper<NotSupportedException> {
    @Override
    public Response toResponse(NotSupportedException e) {

        InformativeResponse response = new InformativeResponse();
        response.message = "Cannot consume content type.";
        response.code = e.getResponse().getStatus();
        return Response.status(e.getResponse().getStatus()).entity(response).build();
    }
}
