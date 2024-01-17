package com.guide.run.global.exception.auth.authorize;

public class NotExistAuthorizationException extends RuntimeException{
    public NotExistAuthorizationException(String message){
        super(message);
    }
    public NotExistAuthorizationException(String message, Throwable cause){
        super(message,cause);
    }
    public NotExistAuthorizationException() {}
}
