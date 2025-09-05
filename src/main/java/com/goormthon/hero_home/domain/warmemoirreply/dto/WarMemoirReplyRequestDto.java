package com.goormthon.hero_home.domain.warmemoirreply.dto;

import com.goormthon.hero_home.domain.user.entity.User;
import com.goormthon.hero_home.domain.warmemoir.entity.WarMemoir;
import com.goormthon.hero_home.domain.warmemoirreply.entity.WarMemoirReply;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "회고록 댓글 생성/수정 요청 DTO")
public class WarMemoirReplyRequestDto {

    @NotBlank(message = "댓글 제목은 필수입니다")
    @Schema(description = "댓글 제목", example = "감동적인 이야기네요")
    private String title;

    @NotBlank(message = "댓글 내용은 필수입니다")
    @Schema(description = "댓글 내용", example = "귀중한 경험을 공유해주셔서 감사합니다.")
    private String content;

    public WarMemoirReply toEntity(WarMemoir warMemoir, User user) {
        return WarMemoirReply.builder()
                .title(this.title)
                .content(this.content)
                .warMemoir(warMemoir)
                .user(user)
                .build();
    }
}