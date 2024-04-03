package org.accounting.system.exceptionmappers;

import jakarta.validation.ValidationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

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
