package com.reconcilem.core.exception;

public class ReconcileMException extends RuntimeException {

    public ReconcileMException(String message){
        super(message);
    }

    public ReconcileMException(String message, Throwable cause){
        super(message, cause);
    }
}