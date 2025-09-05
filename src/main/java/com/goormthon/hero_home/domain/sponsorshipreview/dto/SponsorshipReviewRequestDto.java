package com.goormthon.hero_home.domain.sponsorshipreview.dto;

import lombok.Getter;

public class SponsorshipReviewRequestDto {

    @Getter
    public static class SponsorshipReviewInfo {
        Long sponsorshipBoardId;
        String title;
        String content;
    }
}
