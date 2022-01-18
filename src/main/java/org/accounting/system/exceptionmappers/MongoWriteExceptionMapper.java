package org.accounting.system.exceptionmappers;

import com.mongodb.MongoWriteException;
import org.accounting.system.dtos.InformativeResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MongoWriteExceptionMapper implements ExceptionMapper<MongoWriteException>{
    @Override
    public Response toResponse(MongoWriteException e) {
        InformativeResponse response = new InformativeResponse();
        response.message = "The combination of unit_type and metric_name should be unique. A Metric Definition with that combination has already been created.";
        response.code = Response.Status.CONFLICT.getStatusCode();
        return Response.status(Response.Status.CONFLICT).entity(response).build();
    }
}
