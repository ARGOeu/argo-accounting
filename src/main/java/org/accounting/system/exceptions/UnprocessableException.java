package org.accounting.system.exceptions;

import javax.ws.rs.ClientErrorException;

public class UnprocessableException extends ClientErrorException {

    public UnprocessableException(String message) {
        super(message, 422);
    }
}
