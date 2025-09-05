package com.goormthon.hero_home.domain.user.dto;

import com.goormthon.hero_home.domain.user.entity.Role;
import com.goormthon.hero_home.domain.user.entity.SocialType;
import com.goormthon.hero_home.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "사용자 정보 응답 DTO")
public class UserResponseDto {
    
    @Schema(description = "사용자 ID", example = "1")
    private Long id;
    
    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;
    
    @Schema(description = "이메일 주소", example = "user@example.com")
    private String email;
    
    @Schema(description = "주소", example = "서울시 강남구")
    private String address;
    
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNum;
    
    @Schema(description = "사용자 권한", example = "USER")
    private Role role;
    
    @Schema(description = "소셜 로그인 타입", example = "KAKAO")
    private SocialType socialType;
    
    @Schema(description = "계정 생성일")
    private LocalDateTime createdAt;

    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .address(user.getAddress())
                .phoneNum(user.getPhone_num())
                .role(user.getRole())
                .socialType(user.getSocialType())
                .createdAt(user.getCreatedAt())
                .build();
    }
}