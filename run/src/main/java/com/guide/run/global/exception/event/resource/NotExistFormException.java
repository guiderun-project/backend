package com.guide.run.global.exception.event.resource;

public class NotExistFormException extends RuntimeException{
    public NotExistFormException(String message){
        super(message);
    }
    public NotExistFormException(String message, Throwable cause){
        super(message,cause);
    }
    public NotExistFormException() {}
}
