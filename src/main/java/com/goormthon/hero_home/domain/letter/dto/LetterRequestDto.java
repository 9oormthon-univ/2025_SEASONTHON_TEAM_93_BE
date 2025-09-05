package com.goormthon.hero_home.domain.letter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "편지 생성/수정 요청 DTO")
public class LetterRequestDto {

    @NotBlank(message = "편지 제목은 필수입니다.")
    @Size(max = 200, message = "편지 제목은 200자를 넘을 수 없습니다.")
    @Schema(description = "편지 제목", example = "감사의 마음을 전합니다")
    private String title;

    @NotBlank(message = "편지 내용은 필수입니다.")
    @Size(max = 5000, message = "편지 내용은 5000자를 넘을 수 없습니다.")
    @Schema(description = "편지 내용", example = "어르신의 희생에 깊이 감사드립니다...")
    private String content;

    @Schema(description = "참조할 회고록 ID (선택사항)", example = "1")
    private Long warMemoirId;

    public LetterRequestDto(String title, String content, Long warMemoirId) {
        this.title = title;
        this.content = content;
        this.warMemoirId = warMemoirId;
    }
}