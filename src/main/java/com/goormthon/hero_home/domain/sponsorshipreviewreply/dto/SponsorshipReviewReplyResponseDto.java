package com.goormthon.hero_home.domain.sponsorshipreviewreply.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class SponsorshipReviewReplyResponseDto {

    @Getter
    @Builder
    public static class SponsorshipReviewReplyInfo {
        Long replyId;
        String title;
        String content;
        LocalDate createdAt;
        String email;
    }
}
