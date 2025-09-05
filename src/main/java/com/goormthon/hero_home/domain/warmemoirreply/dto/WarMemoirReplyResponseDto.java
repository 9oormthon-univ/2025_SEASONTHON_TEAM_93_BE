package com.goormthon.hero_home.domain.warmemoirreply.dto;

import com.goormthon.hero_home.domain.warmemoirreply.entity.WarMemoirReply;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "회고록 댓글 응답 DTO")
public class WarMemoirReplyResponseDto {

    @Schema(description = "댓글 ID", example = "1")
    private Long id;

    @Schema(description = "댓글 제목", example = "감동적인 이야기네요")
    private String title;

    @Schema(description = "댓글 내용", example = "귀중한 경험을 공유해주셔서 감사합니다.")
    private String content;

    @Schema(description = "작성자 정보")
    private AuthorDto author;

    @Schema(description = "생성일")
    private LocalDateTime createdAt;

    @Schema(description = "수정일")
    private LocalDateTime updatedAt;

    public static WarMemoirReplyResponseDto from(WarMemoirReply reply) {
        return WarMemoirReplyResponseDto.builder()
                .id(reply.getId())
                .title(reply.getTitle())
                .content(reply.getContent())
                .author(AuthorDto.from(reply.getUser()))
                .createdAt(reply.getCreatedAt())
                .updatedAt(reply.getUpdatedAt())
                .build();
    }

    @Getter
    @Builder
    @Schema(description = "댓글 작성자 정보 DTO")
    public static class AuthorDto {

        @Schema(description = "사용자 ID", example = "1")
        private Long id;

        @Schema(description = "사용자 이름", example = "홍길동")
        private String name;

        @Schema(description = "이메일", example = "user@example.com")
        private String email;

        public static AuthorDto from(com.goormthon.hero_home.domain.user.entity.User user) {
            return AuthorDto.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .build();
        }
    }
}