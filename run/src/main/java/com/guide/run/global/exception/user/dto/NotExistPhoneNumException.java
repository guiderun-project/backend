package com.guide.run.global.exception.user.dto;

public class NotExistPhoneNumException extends RuntimeException{
    public NotExistPhoneNumException (String message){
        super(message);
    }
    public NotExistPhoneNumException (String message, Throwable cause){
        super(message,cause);
    }
    public NotExistPhoneNumException () {}
}
