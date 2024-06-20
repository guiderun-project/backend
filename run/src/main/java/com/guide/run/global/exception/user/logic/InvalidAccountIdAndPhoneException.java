package com.guide.run.global.exception.user.logic;

public class InvalidAccountIdAndPhoneException extends RuntimeException{
    public InvalidAccountIdAndPhoneException (String message){
        super(message);
    }
    public InvalidAccountIdAndPhoneException (String message, Throwable cause){
        super(message,cause);
    }
    public InvalidAccountIdAndPhoneException () {}
}
