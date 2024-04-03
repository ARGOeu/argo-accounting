package org.accounting.system.exceptions;


import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;

public class ConflictException extends ClientErrorException {

    public ConflictException(String message) {
        super(message, Response.Status.CONFLICT);
    }
}
