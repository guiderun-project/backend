package com.guide.run.global.exception.guide;

import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.exception.guide.authorize.NotAuthorityGuideException;
import com.guide.run.global.exception.vi.authorize.NotAuthorityViException;
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
public class GuideAuthorizeExceptionAdvice {
    private final MessageSource messageSource;
    private final ResponseService responseService;

    //3100
    @ExceptionHandler(NotAuthorityGuideException.class)
    protected ResponseEntity<FailResult> NotAuthorityGuideException(NotAuthorityGuideException e){
        return ResponseEntity.status(403).body(responseService.getFailResult(
                getMessage("notAuthorityGuide.code"),
                getMessage("notAuthorityGuide.msg")));
    }

    private String getMessage(String code){
        return getMessage(code,null);
    }

    private String getMessage(String code,Object[] args){
        return messageSource.getMessage(code,args, LocaleContextHolder.getLocale());
    }
}
