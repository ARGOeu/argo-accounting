package org.accounting.system.exceptionmappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.accounting.system.dtos.InformativeResponse;
import org.jboss.logging.Logger;


@Provider
public class GeneralExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(GeneralExceptionMapper.class);

    @Override
    public Response toResponse(Exception e) {

        LOG.error("Server Error", e);

        InformativeResponse response = new InformativeResponse();
        response.message = e.getMessage();
        response.code = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
    }
}
