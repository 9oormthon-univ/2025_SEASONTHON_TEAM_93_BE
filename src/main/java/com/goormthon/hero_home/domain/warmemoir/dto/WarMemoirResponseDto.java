package com.goormthon.hero_home.domain.warmemoir.dto;

import com.goormthon.hero_home.domain.warmemoir.entity.SubWarMemoir;
import com.goormthon.hero_home.domain.warmemoir.entity.WarMemoir;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "참전 회고록 응답 DTO")
public class WarMemoirResponseDto {

    @Schema(description = "회고록 ID", example = "1")
    private Long id;

    @Schema(description = "제목", example = "6.25 전쟁 참전 경험")
    private String title;

    @Schema(description = "대표 이미지 URL", example = "https://example.com/image.jpg")
    private String image;

    @Schema(description = "생성일")
    private LocalDateTime createdAt;

    @Schema(description = "수정일")
    private LocalDateTime updatedAt;

    @Schema(description = "회고록 섹션 목록")
    private List<SubWarMemoirResponseDto> sections;

    @Schema(description = "댓글 개수", example = "5")
    private Long replyCount;

    public static WarMemoirResponseDto from(WarMemoir warMemoir) {
        return WarMemoirResponseDto.builder()
                .id(warMemoir.getId())
                .title(warMemoir.getTitle())
                .image(warMemoir.getImage())
                .createdAt(warMemoir.getCreatedAt())
                .updatedAt(warMemoir.getUpdatedAt())
                .sections(warMemoir.getSubWarMemoirs().stream()
                        .map(SubWarMemoirResponseDto::from)
                        .toList())
                .build();
    }

    public static WarMemoirResponseDto from(WarMemoir warMemoir, Long replyCount) {
        return WarMemoirResponseDto.builder()
                .id(warMemoir.getId())
                .title(warMemoir.getTitle())
                .image(warMemoir.getImage())
                .createdAt(warMemoir.getCreatedAt())
                .updatedAt(warMemoir.getUpdatedAt())
                .sections(warMemoir.getSubWarMemoirs().stream()
                        .map(SubWarMemoirResponseDto::from)
                        .toList())
                .replyCount(replyCount)
                .build();
    }

    @Getter
    @Builder
    @Schema(description = "회고록 섹션 응답 DTO")
    public static class SubWarMemoirResponseDto {

        @Schema(description = "섹션 ID", example = "1")
        private Long id;

        @Schema(description = "섹션 순서", example = "1")
        private Integer sectionOrder;

        @Schema(description = "섹션 제목", example = "입대 과정")
        private String title;

        @Schema(description = "섹션 내용", example = "1950년 6월, 갑작스러운 전쟁 소식을 듣고...")
        private String content;

        public static SubWarMemoirResponseDto from(SubWarMemoir subWarMemoir) {
            return SubWarMemoirResponseDto.builder()
                    .id(subWarMemoir.getId())
                    .sectionOrder(subWarMemoir.getSectionOrder())
                    .title(subWarMemoir.getTitle())
                    .content(subWarMemoir.getContent())
                    .build();
        }
    }
}