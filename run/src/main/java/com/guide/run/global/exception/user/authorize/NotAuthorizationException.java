package com.guide.run.global.exception.user.authorize;

public class NotAuthorizationException extends RuntimeException{
    public NotAuthorizationException(String message){
        super(message);
    }
    public NotAuthorizationException(String message, Throwable cause){
        super(message,cause);
    }
    public NotAuthorizationException() {}
}