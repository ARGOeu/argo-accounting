package org.accounting.system.exceptions;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

public class ConflictException extends ClientErrorException {

    public ConflictException(String message) {
        super(message, Response.Status.CONFLICT);
    }

}
