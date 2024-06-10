package com.guide.run.global.exception.event.logic;

public class NotDeleteEventException extends RuntimeException {
    public NotDeleteEventException(String message){
        super(message);
    }
    public NotDeleteEventException(String message, Throwable cause){
        super(message,cause);
    }
    public NotDeleteEventException() {}
}
