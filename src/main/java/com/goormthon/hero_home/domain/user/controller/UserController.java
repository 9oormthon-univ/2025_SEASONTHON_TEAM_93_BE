package com.goormthon.hero_home.domain.user.controller;

import com.goormthon.hero_home.domain.user.dto.UserResponseDto;
import com.goormthon.hero_home.domain.user.entity.User;
import com.goormthon.hero_home.domain.user.service.UserService;
import com.goormthon.hero_home.global.ApiResponse;
import com.goormthon.hero_home.global.jwt.BlacklistTokenService;
import com.goormthon.hero_home.global.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 관리 API")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BlacklistTokenService blacklistTokenService;

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자 로그아웃 처리 (토큰 블랙리스트 추가)")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (token != null && jwtTokenProvider.validateToken(token)) {
                String email = jwtTokenProvider.getEmailFromToken(token);
                
                // 토큰을 블랙리스트에 추가
                blacklistTokenService.blacklistToken(token);
                
                log.info("사용자 로그아웃: {} (토큰 블랙리스트 추가)", email);
                
                return ResponseEntity.ok(
                    ApiResponse.onSuccess("로그아웃이 완료되었습니다.")
                );
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.onFailure("INVALID_TOKEN", "유효하지 않은 토큰입니다.", null));
            }
        } catch (Exception e) {
            log.error("로그아웃 처리 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.onFailure("LOGOUT_ERROR", "로그아웃 처리 중 오류가 발생했습니다.", null));
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "회원 탈퇴", description = "사용자 계정 삭제 및 토큰 블랙리스트 추가")
    public ResponseEntity<ApiResponse<String>> deleteAccount(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (token != null && jwtTokenProvider.validateToken(token)) {
                String email = jwtTokenProvider.getEmailFromToken(token);
                
                User user = userService.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
                
                // 토큰을 블랙리스트에 추가 (회원 탈퇴 후에도 토큰 사용 방지)
                blacklistTokenService.blacklistToken(token);
                
                // 사용자 계정 삭제
                userService.deleteUser(user.getId());
                
                log.info("회원 탈퇴 완료: {} (토큰 블랙리스트 추가)", email);
                
                return ResponseEntity.ok(
                    ApiResponse.onSuccess("회원 탈퇴가 완료되었습니다.")
                );
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.onFailure("INVALID_TOKEN", "유효하지 않은 토큰입니다.", null));
            }
        } catch (Exception e) {
            log.error("회원 탈퇴 처리 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.onFailure("DELETE_ACCOUNT_ERROR", "회원 탈퇴 처리 중 오류가 발생했습니다.", null));
        }
    }

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자 정보 조회")
    public ResponseEntity<ApiResponse<UserResponseDto>> getCurrentUser(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (token != null && jwtTokenProvider.validateToken(token)) {
                String email = jwtTokenProvider.getEmailFromToken(token);
                
                User user = userService.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
                
                UserResponseDto userResponseDto = UserResponseDto.from(user);
                
                return ResponseEntity.ok(ApiResponse.onSuccess(userResponseDto));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.onFailure("INVALID_TOKEN", "유효하지 않은 토큰입니다.", null));
            }
        } catch (Exception e) {
            log.error("사용자 정보 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.onFailure("GET_USER_ERROR", "사용자 정보 조회 중 오류가 발생했습니다.", null));
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}