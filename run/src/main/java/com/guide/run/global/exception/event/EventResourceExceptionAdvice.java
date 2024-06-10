package com.guide.run.global.exception.event;

import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.exception.event.resource.NotExistCommentException;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import com.guide.run.global.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RequiredArgsConstructor
@RestControllerAdvice
public class EventResourceExceptionAdvice {
    private final MessageSource messageSource;
    private final ResponseService responseService;

    //2300
    @ExceptionHandler(NotExistEventException.class)
    protected ResponseEntity<FailResult> NotExistEventException(NotExistEventException e){
        return ResponseEntity.status(404).body(responseService.getFailResult(
                getMessage("notExistEvent.code"),
                getMessage("notExistEvent.msg")));
    }
    //2301
    @ExceptionHandler(NotExistCommentException.class)
    protected ResponseEntity<FailResult> NotExistCommentException(NotExistCommentException e){
        return ResponseEntity.status(404).body(responseService.getFailResult(
                getMessage("notExistComment.code"),
                getMessage("notExistComment.msg")));
    }

    private String getMessage(String code){
        return getMessage(code,null);
    }

    private String getMessage(String code,Object[] args){
        return messageSource.getMessage(code,args, LocaleContextHolder.getLocale());
    }
}
