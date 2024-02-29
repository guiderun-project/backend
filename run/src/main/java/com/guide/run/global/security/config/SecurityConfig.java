package com.guide.run.global.security.config;

import com.guide.run.global.jwt.JwtAuthenticationFilter;
import com.guide.run.global.jwt.JwtExceptionFilter;
import com.guide.run.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    private final JwtProvider jwtProvider;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> {
            web.ignoring()
                    .requestMatchers("/healty")
                    .requestMatchers("/favicon.ico")
                    .requestMatchers("/member-upload")
                    .requestMatchers("/event-upload")
                    .requestMatchers("/attendance-upload")
                    .requestMatchers("/api/oauth/**");
        };
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
        http
                .csrf(csrf->csrf.disable())
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/api/test").hasRole("VADMIN")
                        .requestMatchers("/api/test2").hasRole("VWAIT")
                        //.requestMatchers("/api/admin/**").hasRole("ADMIN") //일단 각주 처리
                        .requestMatchers("/api/test3").authenticated()
                        .anyRequest().permitAll())

                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtExceptionFilter(),JwtAuthenticationFilter.class);



        return http.build();
    }
}
