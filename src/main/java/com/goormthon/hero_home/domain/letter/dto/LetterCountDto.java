package com.goormthon.hero_home.domain.letter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "회고록별 편지 개수 조회 DTO")
public class LetterCountDto {

    @Schema(description = "회고록 ID", example = "1")
    private Long warMemoirId;

    @Schema(description = "회고록 제목", example = "6.25 전쟁의 기억")
    private String warMemoirTitle;

    @Schema(description = "전체 편지 개수", example = "15")
    private Long letterCount;

    @Schema(description = "완료된 편지 개수", example = "8")
    private Long completedLetterCount;

    @Schema(description = "미완료 편지 개수", example = "7")
    private Long pendingLetterCount;

    public static LetterCountDto of(Long warMemoirId, String warMemoirTitle, Long letterCount, Long completedLetterCount) {
        return LetterCountDto.builder()
                .warMemoirId(warMemoirId)
                .warMemoirTitle(warMemoirTitle)
                .letterCount(letterCount)
                .completedLetterCount(completedLetterCount)
                .pendingLetterCount(letterCount - completedLetterCount)
                .build();
    }
}