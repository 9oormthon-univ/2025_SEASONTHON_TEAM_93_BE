package com.goormthon.hero_home.global.config;

import com.goormthon.hero_home.global.jwt.JwtTokenFilter;
import com.goormthon.hero_home.global.oauth.CustomOAuth2UserService;
import com.goormthon.hero_home.global.oauth.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (JWT 사용)
                .csrf(AbstractHttpConfigurer::disable)
                
                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // 세션 비활성화 (JWT 사용)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // 요청 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        // 인증 없이 접근 가능한 경로
                        .requestMatchers("/", "/login/**").permitAll()
                        .requestMatchers("/oauth2/**").permitAll() // OAuth2 엔드포인트
                        .requestMatchers("/oauth/success.html").permitAll() // OAuth 성공 페이지
                        .requestMatchers("/index.html").permitAll() // 메인 페이지
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll() // 개발용
                        .requestMatchers("/actuator/**").permitAll() // 모니터링용
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // API 문서용
                        .requestMatchers("/error").permitAll() // 오류 페이지
                        
                        // 회고록 관련 - 조회는 인증 불필요, 댓글은 모든 사용자 (본문 권한은 @PreAuthorize로 처리)
                        .requestMatchers("GET", "/warmemoir", "/warmemoir/**").permitAll() // 회고록 조회
                        .requestMatchers("/warmemoir/**").authenticated() // 나머지 회고록 API는 인증 필요 (권한은 @PreAuthorize로 처리)
                        .requestMatchers("POST", "/warmemoir/*/replies").authenticated() // 댓글 작성 - 모든 사용자
                        .requestMatchers("PUT", "/warmemoir/*/replies/**").authenticated() // 댓글 수정 - 모든 사용자
                        .requestMatchers("DELETE", "/warmemoir/*/replies/**").authenticated() // 댓글 삭제 - 모든 사용자
                        
                        // 인증이 필요한 사용자 API
                        .requestMatchers("/user/**").authenticated()
                        // 나머지 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                
                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                )
                
                // JWT 토큰 필터 추가 (AuthorizationFilter 앞에 위치하여 권한 체크 전에 인증 정보 설정)
                .addFilterBefore(jwtTokenFilter, org.springframework.security.web.access.intercept.AuthorizationFilter.class)
                
                // H2 Console 프레임 허용 (개발환경)
                .headers(headers -> headers
                    .frameOptions(frameOptions -> frameOptions.sameOrigin())
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 허용할 출처 (개발환경)
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:8080",
                "https://warhero.site/" // 실제 도메인으로 변경
        ));
        
        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 허용할 헤더
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // 노출할 헤더 설정 (응답 헤더)
        configuration.setExposedHeaders(Arrays.asList("*"));
        
        // 자격 증명 허용
        configuration.setAllowCredentials(true);
        
        // 프리플라이트 요청 캐시 시간 설정
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}