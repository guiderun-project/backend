package com.guide.run.global.service;

import com.guide.run.global.dto.response.FailResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ResponseService {
    public FailResult getFailResult(String code,String msg){
        return new FailResult(code,msg);
    }
}
