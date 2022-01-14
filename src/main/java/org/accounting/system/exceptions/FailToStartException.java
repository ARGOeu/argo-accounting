package org.accounting.system.exceptions;

public class FailToStartException extends RuntimeException{

    public FailToStartException(String message){
        super(message);
    }
}
