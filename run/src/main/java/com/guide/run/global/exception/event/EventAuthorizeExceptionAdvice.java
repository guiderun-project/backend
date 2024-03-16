package com.guide.run.global.exception.event;

import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.exception.event.authorize.NotEventApplierException;
import com.guide.run.global.exception.event.authorize.NotEventOrganizerException;
import com.guide.run.global.exception.event.authorize.NotEventParticipantException;
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
public class EventAuthorizeExceptionAdvice {
    private final MessageSource messageSource;
    private final ResponseService responseService;

    //2100
    @ExceptionHandler(NotEventOrganizerException.class)
    protected ResponseEntity<FailResult> NotEventOrganizerException(NotEventOrganizerException e){
        return ResponseEntity.status(403).body(responseService.getFailResult(
                getMessage("notEventOrganizer.code"),
                getMessage("notEventOrganizer.msg")));
    }

    //2101
    @ExceptionHandler(NotEventApplierException.class)
    protected ResponseEntity<FailResult> NotEventApplierException(NotEventApplierException e){
        return ResponseEntity.status(403).body(responseService.getFailResult(
                getMessage("notEventApplier.code"),
                getMessage("notEventApplier.msg")));
    }

    //2102
    @ExceptionHandler(NotEventParticipantException.class)
    protected ResponseEntity<FailResult> NotEventParticipantException(NotEventParticipantException e){
        return ResponseEntity.status(403).body(responseService.getFailResult(
                getMessage("notEventParticipant.code"),
                getMessage("notEventParticipant.msg")));
    }

    private String getMessage(String code){
        return getMessage(code,null);
    }

    private String getMessage(String code,Object[] args){
        return messageSource.getMessage(code,args, LocaleContextHolder.getLocale());
    }
}
