package com.guide.run.global.exception.event.logic;

public class EventValidationException extends RuntimeException {
    public EventValidationException(String message) {
        super(message);
    }

    public EventValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
