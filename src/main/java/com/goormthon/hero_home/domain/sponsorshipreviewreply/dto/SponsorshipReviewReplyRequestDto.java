package com.goormthon.hero_home.domain.sponsorshipreviewreply.dto;

import lombok.Getter;

public class SponsorshipReviewReplyRequestDto {

    @Getter
    public static class SponsorshipReviewReplyInfo {
        String title;
        String content;
        Long sponsorshipReviewId;
    }
}
