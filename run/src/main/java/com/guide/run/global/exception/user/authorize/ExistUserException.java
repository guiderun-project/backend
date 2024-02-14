package com.guide.run.global.exception.user.authorize;

public class ExistUserException extends RuntimeException{
    public ExistUserException(String message){
        super(message);
    }
    public ExistUserException(String message, Throwable cause){
        super(message,cause);
    }
    public ExistUserException() {}
}
