package com.guide.run.global.exception.auth.authorize;

public class NotValidAccountIdException extends RuntimeException{
    public NotValidAccountIdException(String message){
        super(message);
    }
    public NotValidAccountIdException(String message, Throwable cause){
        super(message,cause);
    }
    public NotValidAccountIdException() {}
}
