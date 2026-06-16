package com.guide.run.global.exception.event.logic;

public class CannotModifyAdditionalQuestionsException extends RuntimeException {
    public CannotModifyAdditionalQuestionsException() {
        super("신청자가 있는 이벤트는 추가정보를 수정할 수 없습니다.");
    }

    public CannotModifyAdditionalQuestionsException(String message) {
        super(message);
    }

    public CannotModifyAdditionalQuestionsException(String message, Throwable cause) {
        super(message, cause);
    }
}
