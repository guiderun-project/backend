package com.guide.run.global.exception.user.dto;

public class NotAgreeTermException extends RuntimeException{
    public NotAgreeTermException(String message){
        super(message);
    }
    public NotAgreeTermException(String message, Throwable cause){
        super(message,cause);
    }
    public NotAgreeTermException() {}
}
