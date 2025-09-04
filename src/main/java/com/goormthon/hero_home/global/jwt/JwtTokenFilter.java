package com.goormthon.hero_home.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends GenericFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final BlacklistTokenService blacklistTokenService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        try {
            String token = extractTokenFromRequest(httpRequest);
            String requestUri = httpRequest.getRequestURI();
            
            // 인증이 필요한 경로인지 확인
            if (requiresAuthentication(requestUri)) {
                if (token == null) {
                    log.warn("인증이 필요한 경로에 토큰이 없음: {}", requestUri);
                    sendUnauthorizedResponse(httpResponse, "인증 토큰이 필요합니다.");
                    return;
                }
                
                if (!jwtTokenProvider.validateToken(token)) {
                    log.warn("유효하지 않은 토큰: {}", requestUri);
                    sendUnauthorizedResponse(httpResponse, "유효하지 않은 토큰입니다.");
                    return;
                }
                
                if (blacklistTokenService.isTokenBlacklisted(token)) {
                    log.warn("블랙리스트된 토큰 사용 시도: {}", requestUri);
                    sendUnauthorizedResponse(httpResponse, "사용할 수 없는 토큰입니다.");
                    return;
                }
            }
            
            if (token != null && jwtTokenProvider.validateToken(token) && !blacklistTokenService.isTokenBlacklisted(token)) {
                // JWT에서 사용자 정보 추출
                String email = jwtTokenProvider.getEmailFromToken(token);
                String role = jwtTokenProvider.getRoleFromToken(token);
                
                // Authentication 객체 생성
                List<GrantedAuthority> authorities = new ArrayList<>();
                // Role enum에 이미 ROLE_ 접두사가 있는지 확인하고 처리
                if (!role.startsWith("ROLE_")) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                } else {
                    authorities.add(new SimpleGrantedAuthority(role));
                }
                
                UserDetails userDetails = new User(email, "", authorities);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, token, authorities);
                
                // SecurityContext에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("JWT 인증 성공: email={}, role={}", email, role);
            }
            
            chain.doFilter(request, response);
            
        } catch (Exception e) {
            log.error("JWT 토큰 처리 중 오류 발생", e);
            SecurityContextHolder.clearContext();
            sendUnauthorizedResponse(httpResponse, "인증 처리 중 오류가 발생했습니다.");
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
    
    private boolean requiresAuthentication(String requestUri) {
        return requestUri.startsWith("/user/");
    }
    
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}