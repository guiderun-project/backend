package com.guide.run.global.security.config;

import com.guide.run.global.jwt.JwtAuthenticationFilter;
import com.guide.run.global.jwt.JwtExceptionFilter;
import com.guide.run.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;



@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    private static final List<String> DEFAULT_ALLOWED_ORIGINS = List.of(
            "https://dev.guiderun.org",
            "https://guiderun.org",
            "https://www.guiderun.org"
    );

    private final JwtProvider jwtProvider;

    @Value("${cors.allowed-origins:${cors.origin:}}")
    private String origin;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> {
            web.ignoring()
                    .requestMatchers("/health")
                    .requestMatchers("/webhook/tosspayments")
                    .requestMatchers("/webhook/appsmith/user-approval")
                    .requestMatchers("/favicon.ico");
        };
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
        http
                .csrf(csrf->csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/signup/**").hasRole("NEW")
                        .requestMatchers("/api/oauth/login/reissue").permitAll()
                        .requestMatchers(
                                "/api/sms/**",
                                "/api/oauth/**",
                                "/api/logout/**",
                                "/api/login/**",
                                "/api/accountId/**",
                                "/api/new-password/**").permitAll()
                        // RegexRequestMatcher는 쿼리스트링까지 포함해 비교하므로 끝에 (\?.*)? 를 둬야 ?tab=... 가 붙어도 매칭된다.
                        .requestMatchers(new RegexRequestMatcher("^/api/event/[0-9]+(\\?.*)?$", "GET")).permitAll()
                        // 비회원도 조회 가능한 공개 목록/검색/댓글 API (GET 한정)
                        .requestMatchers(new RegexRequestMatcher("^/api/event/summary(\\?.*)?$", "GET")).permitAll()
                        .requestMatchers(new RegexRequestMatcher("^/api/event/all(\\?.*)?$", "GET")).permitAll()
                        .requestMatchers(new RegexRequestMatcher("^/api/event/search(\\?.*)?$", "GET")).permitAll()
                        .requestMatchers(new RegexRequestMatcher("^/api/event/upcoming(\\?.*)?$", "GET")).permitAll()
                        .requestMatchers(new RegexRequestMatcher("^/api/event/[0-9]+/comments(\\?.*)?$", "GET")).permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(
                                "/api/user/account",
                                "/api/user/account/duplicated",
                                "/api/user/mypage",
                                "/api/user/personal/**",
                                "/api/user/permission/**",
                                "/api/user/running/**",
                                "/api/withdrawal/**",
                                "/api/user/img/**")
                        .hasAnyRole("ADMIN", "USER", "COACH", "WAIT", "REJECT")

                        .requestMatchers("/api/**").hasAnyRole("ADMIN", "USER", "COACH")

                        .anyRequest().permitAll())

                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtExceptionFilter(),JwtAuthenticationFilter.class);



        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        allowedOrigins().forEach(config::addAllowedOrigin);
        config.addAllowedMethod("*"); // 모든 메소드 허용.
        config.addAllowedHeader("*");
        config.setMaxAge(3600L);
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private Set<String> allowedOrigins() {
        Set<String> allowedOrigins = new LinkedHashSet<>(DEFAULT_ALLOWED_ORIGINS);
        if (origin == null || origin.isBlank()) {
            return allowedOrigins;
        }

        Arrays.stream(origin.split(","))
                .map(String::trim)
                .filter(configuredOrigin -> !configuredOrigin.isEmpty())
                .forEach(allowedOrigins::add);
        return allowedOrigins;
    }
}
