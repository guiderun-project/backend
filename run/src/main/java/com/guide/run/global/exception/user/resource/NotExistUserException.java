package com.guide.run.global.exception.user.resource;


import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class NotExistUserException extends UsernameNotFoundException {
    public NotExistUserException(String message){
        super(message);
    }
    public NotExistUserException(String message, Throwable cause){
        super(message,cause);
    }
    public NotExistUserException(){
        this("존재하지 않는 사용자 입니다.");
    }
}
