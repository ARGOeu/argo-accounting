package org.accounting.system.exceptionmappers;


import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.accounting.system.dtos.InformativeResponse;
import org.jboss.logging.Logger;


@Provider
public class IllegalArgumentMapper implements ExceptionMapper<IllegalArgumentException> {

    private static final Logger LOG = Logger.getLogger(IllegalArgumentMapper.class);

    @Override
    public Response toResponse(IllegalArgumentException e) {

        LOG.error("Illegal Argument Error", e);

        InformativeResponse response = new InformativeResponse();
        response.message = e.getMessage();
        response.code = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
    }
}
