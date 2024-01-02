package com.guide.run.gloabl.config;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
    @Bean
    public Gson gson(){
        return new Gson();
    }
}
