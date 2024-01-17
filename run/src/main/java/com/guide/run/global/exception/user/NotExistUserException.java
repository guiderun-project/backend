package com.guide.run.global.exception.user;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class NotExistUserException extends RuntimeException{
    public NotExistUserException(String message){
        super(message);
    }
    public NotExistUserException(String message, Throwable cause){
        super(message,cause);
    }

    public NotExistUserException() {
    }
}
