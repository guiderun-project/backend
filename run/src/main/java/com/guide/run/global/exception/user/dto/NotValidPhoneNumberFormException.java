package com.guide.run.global.exception.user.dto;

public class NotValidPhoneNumberFormException extends RuntimeException{
    public NotValidPhoneNumberFormException(String message){
        super(message);
    }
    public NotValidPhoneNumberFormException(String message, Throwable cause){
        super(message,cause);
    }
    public NotValidPhoneNumberFormException() {}
}
