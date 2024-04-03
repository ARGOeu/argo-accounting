package org.accounting.system.exceptionmappers;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.accounting.system.dtos.InformativeResponse;
import org.jboss.logging.Logger;

@Provider
public class ClientErrorExceptionMapper implements ExceptionMapper<ClientErrorException> {

    private static final Logger LOG = Logger.getLogger(ClientErrorExceptionMapper.class);

    @Override
    public Response toResponse(ClientErrorException e) {

        LOG.error("Web Application Error", e);

        InformativeResponse response = new InformativeResponse();
        response.message = e.getMessage();
        response.code = e.getResponse().getStatus();
        return Response.status(e.getResponse().getStatus()).entity(response).build();
    }
}
