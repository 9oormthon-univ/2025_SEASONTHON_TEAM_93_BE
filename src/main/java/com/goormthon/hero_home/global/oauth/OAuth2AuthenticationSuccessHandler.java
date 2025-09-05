package com.goormthon.hero_home.global.oauth;

import com.goormthon.hero_home.domain.user.entity.User;
import com.goormthon.hero_home.domain.user.repository.UserRepository;
import com.goormthon.hero_home.global.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Value("${app.oauth2.success-redirect-url:http://localhost:3000/oauth/success}")
    private String successRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                      Authentication authentication) throws IOException, ServletException {
        
        log.info("OAuth2 로그인 성공 처리 시작");
        
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        
        // 카카오 사용자 ID로 사용자 조회
        String socialId = String.valueOf(oauth2User.getAttributes().get("id"));
        User user = userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + socialId));
        
        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole().toString());
        
        log.info("JWT 토큰 생성 완료. 사용자: {}", user.getEmail());
        
        // 프론트엔드로 리다이렉트 (토큰 포함)
        String redirectUrl = String.format("%s?token=%s&email=%s&id=%d", 
                successRedirectUrl, token, user.getEmail(), user.getId());
        
        log.info("리다이렉트 URL: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}