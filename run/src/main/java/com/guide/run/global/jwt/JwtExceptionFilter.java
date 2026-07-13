package com.guide.run.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.exception.auth.authorize.NotExistAuthorizationException;
import com.guide.run.global.exception.auth.authorize.NotValidAccessTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Slf4j
@NoArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {
    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        try{
            filterChain.doFilter(request, response);
        } catch (NotValidAccessTokenException e){
            sendError(request, response, "0100", "유효하지 않은 액세스토큰입니다.");
            log.error("유효하지 않은 엑세스 토큰");
            return;
        }catch (NotExistAuthorizationException e){
            sendError(request, response, "0101", "인증할 수 있는 사용자 데이터가 존재하지 않습니다");
            log.error("엑세스 토큰 정보로 찾을 수 있는 유저가 없습니다");
            return;
        }
    }

    private static void sendError
            (HttpServletRequest request, HttpServletResponse response, String number, String message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(401);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        FailResult result = new FailResult(
                number,
                message,
                401,
                request.getRequestURI(),
                OffsetDateTime.now(SEOUL_ZONE).toString()
        );
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
