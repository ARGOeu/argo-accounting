package org.accounting.system.exceptionmappers;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.accounting.system.dtos.InformativeResponse;
import org.jboss.logging.Logger;

@Provider
public class InvalidFormatExceptionMapper implements ExceptionMapper<InvalidFormatException> {

    private static final Logger LOG = Logger.getLogger(InvalidFormatExceptionMapper.class);

    @Override
    public Response toResponse(InvalidFormatException e) {

        LOG.error("Invalid Format Error", e);

        InformativeResponse response = new InformativeResponse();
        response.message = e.getCause().getMessage();
        response.code = Response.Status.BAD_REQUEST.getStatusCode();
        return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
    }
}
