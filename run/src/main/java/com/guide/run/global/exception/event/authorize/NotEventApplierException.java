package com.guide.run.global.exception.event.authorize;

public class NotEventApplierException extends RuntimeException{
    public NotEventApplierException(String message){
        super(message);
    }
    public NotEventApplierException(String message, Throwable cause){
        super(message,cause);
    }
    public NotEventApplierException() {}
}
