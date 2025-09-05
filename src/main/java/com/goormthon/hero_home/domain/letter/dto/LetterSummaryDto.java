package com.goormthon.hero_home.domain.letter.dto;

import com.goormthon.hero_home.domain.letter.entity.Letter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "편지 목록 조회용 요약 DTO")
public class LetterSummaryDto {

    @Schema(description = "편지 ID", example = "1")
    private Long id;

    @Schema(description = "편지 제목", example = "감사의 마음을 전합니다")
    private String title;

    @Schema(description = "편지 내용 미리보기 (100자)", example = "어르신의 희생에 깊이 감사드립니다...")
    private String contentPreview;

    @Schema(description = "완료 여부", example = "false")
    private Boolean isCompleted;

    @Schema(description = "작성자 이름", example = "김철수")
    private String authorName;

    @Schema(description = "참조 회고록 제목", example = "6.25 전쟁의 기억")
    private String warMemoirTitle;

    @Schema(description = "작성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    public static LetterSummaryDto from(Letter letter) {
        String contentPreview = letter.getContent().length() > 100 
                ? letter.getContent().substring(0, 100) + "..."
                : letter.getContent();

        return LetterSummaryDto.builder()
                .id(letter.getId())
                .title(letter.getTitle())
                .contentPreview(contentPreview)
                .isCompleted(letter.getIsCompleted())
                .authorName(letter.getUser().getName())
                .warMemoirTitle(letter.getWarMemoir() != null ? 
                        letter.getWarMemoir().getTitle() : null)
                .createdAt(letter.getCreatedAt())
                .updatedAt(letter.getUpdatedAt())
                .build();
    }
}