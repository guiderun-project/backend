package com.guide.run.global.security.config;

import com.guide.run.global.jwt.JwtAuthenticationFilter;
import com.guide.run.global.jwt.JwtExceptionFilter;
import com.guide.run.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity(securedEnabled = true)
@EnableConfigurationProperties(CorsProperties.class)
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final CorsProperties corsProperties;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> {
             web.ignoring()
                .requestMatchers("/favicon.ico")
                .requestMatchers("/v3/api-docs/**")
                .requestMatchers("/v3/api-docs")
                .requestMatchers("/swagger-ui/**")
                .requestMatchers("/swagger-ui.html");
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
                        .requestMatchers("/api/oauth/login/reissue").hasAnyRole("ADMIN", "USER", "COACH", "WAIT", "REJECT")
                        .requestMatchers(
                                "/api/sms/**",
                                "/api/oauth/**",
                                "/api/logout/**",
                                "/api/login/**",
                                "/api/accountId/**",
                                "/api/new-password/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/health").permitAll()
                        .requestMatchers(
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
        CorsConfiguration healthConfig = new CorsConfiguration();
        healthConfig.setAllowedOriginPatterns(List.of("*"));
        healthConfig.setAllowedMethods(List.of("GET", "OPTIONS"));
        healthConfig.setAllowedHeaders(List.of("*"));
        healthConfig.setMaxAge(3600L);
        healthConfig.setAllowCredentials(false);

        CorsConfiguration apiConfig = new CorsConfiguration();
        apiConfig.setAllowedOrigins(corsProperties.getAllowedOrigins());
        apiConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        apiConfig.setAllowedHeaders(List.of("*"));
        apiConfig.setMaxAge(3600L);
        apiConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/health", healthConfig);
        source.registerCorsConfiguration("/health/**", healthConfig);
        source.registerCorsConfiguration("/**", apiConfig);
        return source;
    }
}
