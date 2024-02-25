package com.guide.run.global.exception.coach.authorize;

public class NotAuthorityCoachException extends RuntimeException{
    public NotAuthorityCoachException(String message){
        super(message);
    }
    public NotAuthorityCoachException(String message, Throwable cause){
        super(message,cause);
    }
    public NotAuthorityCoachException() {}
}
