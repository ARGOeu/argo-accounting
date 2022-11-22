package org.accounting.system.exceptionmappers;

import org.accounting.system.dtos.InformativeResponse;
import org.jboss.logging.Logger;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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
