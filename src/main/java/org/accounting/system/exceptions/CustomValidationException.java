package org.accounting.system.exceptions;

import io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.validation.ValidationException;


public class CustomValidationException extends ValidationException {

    private HttpResponseStatus status;

    public CustomValidationException(String message, HttpResponseStatus status){
        super(message);
        this.status = status;
    }

    public HttpResponseStatus getStatus() {
        return status;
    }
}
