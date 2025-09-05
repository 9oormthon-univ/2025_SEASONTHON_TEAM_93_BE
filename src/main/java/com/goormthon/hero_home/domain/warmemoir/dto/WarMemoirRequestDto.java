package com.goormthon.hero_home.domain.warmemoir.dto;

import com.goormthon.hero_home.domain.warmemoir.entity.SubWarMemoir;
import com.goormthon.hero_home.domain.warmemoir.entity.WarMemoir;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.IntStream;

@Getter
@NoArgsConstructor
@Schema(description = "참전 회고록 생성/수정 요청 DTO")
public class WarMemoirRequestDto {

    @NotBlank(message = "제목은 필수입니다")
    @Schema(description = "회고록 제목", example = "6.25 전쟁 참전 경험")
    private String title;

    @Schema(description = "대표 이미지 URL", example = "https://example.com/image.jpg")
    private String image;

    @Valid
    @NotEmpty(message = "섹션은 최소 1개 이상 필요합니다")
    @Schema(description = "회고록 섹션 목록")
    private List<SubWarMemoirRequestDto> sections;

    public WarMemoir toEntity() {
        return WarMemoir.builder()
                .title(this.title)
                .image(this.image)
                .build();
    }

    public List<SubWarMemoir> toSubWarMemoirEntities(WarMemoir warMemoir) {
        return IntStream.range(0, sections.size())
                .mapToObj(i -> {
                    SubWarMemoirRequestDto sectionDto = sections.get(i);
                    return SubWarMemoir.builder()
                            .sectionOrder(i + 1)
                            .title(sectionDto.getTitle())
                            .content(sectionDto.getContent())
                            .warMemoir(warMemoir)
                            .build();
                })
                .toList();
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "회고록 섹션 요청 DTO")
    public static class SubWarMemoirRequestDto {

        @NotBlank(message = "섹션 제목은 필수입니다")
        @Schema(description = "섹션 제목", example = "입대 과정")
        private String title;

        @NotBlank(message = "섹션 내용은 필수입니다")
        @Schema(description = "섹션 내용", example = "1950년 6월, 갑작스러운 전쟁 소식을 듣고...")
        private String content;
    }
}