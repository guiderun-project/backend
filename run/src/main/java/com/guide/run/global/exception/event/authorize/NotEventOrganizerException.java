package com.guide.run.global.exception.event.authorize;

public class NotEventOrganizerException extends RuntimeException{
    public NotEventOrganizerException(String message){
        super(message);
    }
    public NotEventOrganizerException(String message, Throwable cause){
        super(message,cause);
    }
    public NotEventOrganizerException() {}
}
