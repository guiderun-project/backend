package com.guide.run.global.exception.user.authorize;

public class UnauthorizedUserException extends RuntimeException{
    public UnauthorizedUserException(String message){
        super(message);
    }
    public UnauthorizedUserException(String message, Throwable cause){
        super(message,cause);
    }
    public UnauthorizedUserException() {}
}
