package org.accounting.system.exceptionmappers;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import javax.validation.ValidationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class RestClientExceptionMapper implements ResponseExceptionMapper<RuntimeException> {
    
    @Override
    public RuntimeException toThrowable(Response response) {

        int status = response.getStatus();

        RuntimeException re ;
        switch (status) {
            case 404: re = new ValidationException("Cannot found the requested resource.");
                break;
            default:
                re = new WebApplicationException("Communication error.");
        }
        return re;
    }
}
