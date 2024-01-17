package com.guide.run.global.exception.event.authorize;

public class NotEventParticipantException extends RuntimeException{
    public NotEventParticipantException(String message){
        super(message);
    }
    public NotEventParticipantException(String message, Throwable cause){
        super(message,cause);
    }
    public NotEventParticipantException() {}
}
