package com.guide.run.global.exception.event.dto;

public class NotValidEventRecruitException extends RuntimeException {
    public NotValidEventRecruitException(String message){
        super(message);
    }
    public NotValidEventRecruitException(String message, Throwable cause){
        super(message,cause);
    }
    public NotValidEventRecruitException() {}
}
