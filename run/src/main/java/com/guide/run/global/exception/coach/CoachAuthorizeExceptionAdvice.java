package com.guide.run.global.exception.coach;

import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.exception.admin.authorize.NotAuthorityAdminException;
import com.guide.run.global.exception.coach.authorize.NotAuthorityCoachException;
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
public class CoachAuthorizeExceptionAdvice {
    private final MessageSource messageSource;
    private final ResponseService responseService;

    //5100
    @ExceptionHandler(NotAuthorityCoachException.class)
    protected ResponseEntity<FailResult> NotAuthorityCoachException(NotAuthorityCoachException e){
        return ResponseEntity.status(403).body(responseService.getFailResult(
                getMessage("notAuthorityAdmin.code"),
                getMessage("notAuthorityAdmin.msg")));
    }

    private String getMessage(String code){
        return getMessage(code,null);
    }

    private String getMessage(String code,Object[] args){
        return messageSource.getMessage(code,args, LocaleContextHolder.getLocale());
    }
}
