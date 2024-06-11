package org.accounting.system.exceptionmappers;

import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.accounting.system.dtos.InformativeResponse;
import org.jboss.logging.Logger;


@Provider
public class ServerErrorExceptionMapper implements ExceptionMapper<ServerErrorException> {

    private static final Logger LOG = Logger.getLogger(ServerErrorExceptionMapper.class);

    @Override
    public Response toResponse(ServerErrorException e) {

        LOG.error("Server Error", e);

        var response = new InformativeResponse();
        response.message = e.getMessage();
        response.code = e.getResponse().getStatus();
        return Response.status(e.getResponse().getStatus()).entity(response).build();
    }
}
