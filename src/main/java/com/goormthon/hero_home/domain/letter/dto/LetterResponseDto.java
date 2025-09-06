package com.goormthon.hero_home.domain.letter.dto;

import com.goormthon.hero_home.domain.letter.entity.Letter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "편지 상세 정보 응답 DTO")
public class LetterResponseDto {

    @Schema(description = "편지 ID", example = "1")
    private Long id;

    @Schema(description = "편지 제목", example = "감사의 마음을 전합니다")
    private String title;

    @Schema(description = "편지 내용", example = "어르신의 희생에 깊이 감사드립니다...")
    private String content;

    @Schema(description = "완료 여부", example = "false")
    private Boolean isCompleted;

    @Schema(description = "작성자 정보")
    private AuthorInfo author;

    @Schema(description = "참조 회고록 정보")
    private WarMemoirInfo warMemoir;

    @Schema(description = "작성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    @Getter
    @Builder
    @Schema(description = "작성자 정보")
    public static class AuthorInfo {
        @Schema(description = "사용자 ID", example = "1")
        private Long id;
        
        @Schema(description = "사용자 이름", example = "김철수")
        private String name;
        
        @Schema(description = "사용자 이메일", example = "user@example.com")
        private String email;
    }

    @Getter
    @Builder
    @Schema(description = "참조 회고록 정보")
    public static class WarMemoirInfo {
        @Schema(description = "회고록 ID", example = "1")
        private Long id;
        
        @Schema(description = "회고록 제목", example = "6.25 전쟁의 기억")
        private String title;
    }

    public static LetterResponseDto from(Letter letter) {
        return LetterResponseDto.builder()
                .id(letter.getId())
                .title(letter.getTitle())
                .content(letter.getContent())
                .isCompleted(letter.getIsCompleted())
                .author(AuthorInfo.builder()
                        .id(letter.getUser().getId())
                        .name(letter.getUser().getName())
                        .email(letter.getUser().getEmail())
                        .build())
                .warMemoir(letter.getWarMemoir() != null ? 
                        WarMemoirInfo.builder()
                                .id(letter.getWarMemoir().getId())
                                .title(letter.getWarMemoir().getTitle())
                                .build() : null)
                .createdAt(letter.getCreatedAt())
                .updatedAt(letter.getUpdatedAt())
                .build();
    }
}