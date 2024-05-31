package com.guide.run.global.exception.event;

import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.exception.event.authorize.NotEventOrganizerException;
import com.guide.run.global.exception.event.logic.*;
import com.guide.run.global.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class EventLogicExceptionAdvice {
    private final MessageSource messageSource;
    private final ResponseService responseService;

    //2200
    @ExceptionHandler(NotValidSortException.class)
    protected ResponseEntity<FailResult> NotValidSortException(NotValidSortException e){
        return ResponseEntity.status(400).body(responseService.getFailResult(
                getMessage("notValidSort.code"),
                getMessage("notValidSort.msg")));
    }
    //2201
    @ExceptionHandler(NotValidTypeException.class)
    protected ResponseEntity<FailResult> NotValidTypeException(NotValidTypeException e){
        return ResponseEntity.status(400).body(responseService.getFailResult(
                getMessage("notValidType.code"),
                getMessage("notValidType.msg")));
    }
    //2202
    @ExceptionHandler(NotValidKindException.class)
    protected ResponseEntity<FailResult> NotValidKindException(NotValidKindException e){
        return ResponseEntity.status(400).body(responseService.getFailResult(
                getMessage("notValidKind.code"),
                getMessage("notValidKind.msg")));
    }
    //2204
    @ExceptionHandler(NotValidYearException.class)
    protected ResponseEntity<FailResult> NotValidYearException(NotValidYearException e){
        return ResponseEntity.status(400).body(responseService.getFailResult(
                getMessage("notValidYear.code"),
                getMessage("notValidYear.msg")));
    }
    //2205
    @ExceptionHandler(NotValidMonthException.class)
    protected ResponseEntity<FailResult> NotValidMonthException(NotValidMonthException e){
        return ResponseEntity.status(400).body(responseService.getFailResult(
                getMessage("notValidMonth.code"),
                getMessage("notValidMonth.msg")));
    }
    //2206
    @ExceptionHandler(NotValidDayException.class)
    protected ResponseEntity<FailResult> NotValidDayException(NotValidDayException e){
        return ResponseEntity.status(400).body(responseService.getFailResult(
                getMessage("notValidDay.code"),
                getMessage("notValidDay.msg")));
    }

    private String getMessage(String code){
        return getMessage(code,null);
    }

    private String getMessage(String code,Object[] args){
        return messageSource.getMessage(code,args, LocaleContextHolder.getLocale());
    }
}
