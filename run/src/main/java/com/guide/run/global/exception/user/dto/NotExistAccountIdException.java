package com.guide.run.global.exception.user.dto;

public class NotExistAccountIdException extends RuntimeException{
    public NotExistAccountIdException (String message){
        super(message);
    }
    public NotExistAccountIdException (String message, Throwable cause){
        super(message,cause);
    }
    public NotExistAccountIdException () {}
}
