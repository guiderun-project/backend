package com.guide.run.global.exception.event.resource;

public class NotExistEventException extends RuntimeException{
    public NotExistEventException(String message){
        super(message);
    }
    public NotExistEventException(String message, Throwable cause){
        super(message,cause);
    }
    public NotExistEventException() {}
}
