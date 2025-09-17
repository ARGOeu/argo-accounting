package org.accounting.system.services.clients;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

public class KeycloakExceptionMapper implements ResponseExceptionMapper<RuntimeException> {

    @Override
    public RuntimeException toThrowable(Response response) {
        int status = response.getStatus();
        switch (status) {
            case 401:
                return new RuntimeException("Unauthorized group management client. Status : "+ status);
            case 403:
                return new RuntimeException("Forbidden group management client. Status : "+ status);
            case 404:
                return new RuntimeException("Not found. Status : "+ status);
            default:
                return new RuntimeException("HTTP group management error. Status : " + status);
        }
    }

    @Override
    public boolean handles(int status, MultivaluedMap<String, Object> headers) {
        return status >= 400;
    }
}