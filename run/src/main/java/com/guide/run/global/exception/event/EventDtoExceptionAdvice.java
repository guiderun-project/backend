package com.guide.run.global.exception.event;

import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.exception.event.dto.NotValidEventRecruitException;
import com.guide.run.global.exception.event.dto.NotValidEventStartException;
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
public class EventDtoExceptionAdvice {
    private final MessageSource messageSource;
    private final ResponseService responseService;

    //2000
    @ExceptionHandler(NotValidEventStartException.class)
    protected ResponseEntity<FailResult> NotValidEventStartException(NotValidEventStartException e){
        return ResponseEntity.status(404).body(responseService.getFailResult(
                getMessage("notValidStart.code"),
                getMessage("notValidStart.msg")));
    }
    //2001
    @ExceptionHandler(NotValidEventRecruitException.class)
    protected ResponseEntity<FailResult> NotValidEventRecruitException(NotValidEventRecruitException e){
        return ResponseEntity.status(404).body(responseService.getFailResult(
                getMessage("notValidRecruit.code"),
                getMessage("notValidRecruit.msg")));
    }

    private String getMessage(String code){
        return getMessage(code,null);
    }

    private String getMessage(String code,Object[] args){
        return messageSource.getMessage(code,args, LocaleContextHolder.getLocale());
    }
}
