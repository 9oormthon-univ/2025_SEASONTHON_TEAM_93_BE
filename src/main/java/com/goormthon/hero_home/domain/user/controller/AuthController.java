package com.goormthon.hero_home.domain.user.controller;

import com.goormthon.hero_home.domain.user.dto.AccessTokenDto;
import com.goormthon.hero_home.domain.user.dto.KakaoProfileDto;
import com.goormthon.hero_home.domain.user.dto.RedirectDto;
import com.goormthon.hero_home.domain.user.entity.SocialType;
import com.goormthon.hero_home.domain.user.entity.User;
import com.goormthon.hero_home.domain.user.service.KakaoService;
import com.goormthon.hero_home.domain.user.service.UserService;
import com.goormthon.hero_home.global.ApiResponse;
import com.goormthon.hero_home.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AuthController {

    private final KakaoService kakaoService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/kakao/doLogin")
    public ResponseEntity<ApiResponse<Map<String, Object>>> kakaoLogin(@RequestBody RedirectDto redirectDto) {
        try {
            // 1. 카카오로부터 액세스 토큰 획득
            AccessTokenDto accessTokenDto = kakaoService.getAccessToken(redirectDto.getCode());
            
            // 2. 액세스 토큰으로 카카오 사용자 프로필 조회
            KakaoProfileDto kakaoProfileDto = kakaoService.getKakaoProfile(accessTokenDto.getAccess_token());
            
            // 3. 기존 사용자 조회 또는 신규 사용자 생성
            User originalUser = userService.getUserBySocialId(kakaoProfileDto.getId());
            if (originalUser == null) {
                originalUser = userService.createOauth(
                    kakaoProfileDto.getId(), 
                    kakaoProfileDto.getKakao_account().getEmail(), 
                    SocialType.KAKAO
                );
            }
            
            // 4. JWT 토큰 생성
            String jwtToken = jwtTokenProvider.createToken(originalUser.getEmail(), originalUser.getRole().toString());
            
            // 5. 응답 데이터 구성
            Map<String, Object> loginInfo = new HashMap<>();
            loginInfo.put("id", originalUser.getId());
            loginInfo.put("email", originalUser.getEmail());
            loginInfo.put("token", jwtToken);
            
            return ResponseEntity.ok(ApiResponse.onSuccess(loginInfo));
            
        } catch (Exception e) {
            log.error("카카오 로그인 처리 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure("KAKAO_LOGIN_ERROR", "카카오 로그인 처리 중 오류가 발생했습니다.", null));
        }
    }
}