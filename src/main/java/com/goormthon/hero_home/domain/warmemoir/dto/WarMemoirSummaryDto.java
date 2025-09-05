package com.goormthon.hero_home.domain.warmemoir.dto;

import com.goormthon.hero_home.domain.warmemoir.entity.WarMemoir;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "참전 회고록 요약 DTO (목록 조회용)")
public class WarMemoirSummaryDto {

    @Schema(description = "회고록 ID", example = "1")
    private Long id;

    @Schema(description = "제목", example = "6.25 전쟁 참전 경험")
    private String title;

    @Schema(description = "대표 이미지 URL", example = "https://example.com/image.jpg")
    private String image;

    @Schema(description = "생성일")
    private LocalDateTime createdAt;

    @Schema(description = "댓글 개수", example = "5")
    private Long replyCount;

    @Schema(description = "섹션 개수", example = "3")
    private Integer sectionCount;

    public static WarMemoirSummaryDto from(WarMemoir warMemoir) {
        return WarMemoirSummaryDto.builder()
                .id(warMemoir.getId())
                .title(warMemoir.getTitle())
                .image(warMemoir.getImage())
                .createdAt(warMemoir.getCreatedAt())
                .sectionCount(warMemoir.getSubWarMemoirs().size())
                .build();
    }

    public static WarMemoirSummaryDto from(WarMemoir warMemoir, Long replyCount) {
        return WarMemoirSummaryDto.builder()
                .id(warMemoir.getId())
                .title(warMemoir.getTitle())
                .image(warMemoir.getImage())
                .createdAt(warMemoir.getCreatedAt())
                .sectionCount(warMemoir.getSubWarMemoirs().size())
                .replyCount(replyCount)
                .build();
    }
}