package org.accounting.system.exceptionmappers;

import com.mongodb.MongoWriteException;
import org.accounting.system.dtos.InformativeResponse;
import org.jboss.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MongoWriteExceptionMapper implements ExceptionMapper<MongoWriteException>{

    private static final Logger LOG = Logger.getLogger(MongoWriteExceptionMapper.class);

    @Override
    public Response toResponse(MongoWriteException e) {

        LOG.error("Mongo Error", e);

        InformativeResponse response = new InformativeResponse();
        response.message = e.getMessage();
        response.code = Response.Status.CONFLICT.getStatusCode();
        return Response.status(Response.Status.CONFLICT).entity(response).build();
    }
}
