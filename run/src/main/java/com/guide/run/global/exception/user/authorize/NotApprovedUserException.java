package com.guide.run.global.exception.user.authorize;

public class NotApprovedUserException extends RuntimeException{
    public NotApprovedUserException(String message){
        super(message);
    }
    public NotApprovedUserException(String message, Throwable cause){
        super(message,cause);
    }
    public NotApprovedUserException() {}
}

