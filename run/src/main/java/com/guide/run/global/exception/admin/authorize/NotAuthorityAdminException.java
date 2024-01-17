package com.guide.run.global.exception.admin.authorize;

public class NotAuthorityAdminException extends RuntimeException{
    public NotAuthorityAdminException(String message){
        super(message);
    }
    public NotAuthorityAdminException(String message, Throwable cause){
        super(message,cause);
    }
    public NotAuthorityAdminException() {}
}
