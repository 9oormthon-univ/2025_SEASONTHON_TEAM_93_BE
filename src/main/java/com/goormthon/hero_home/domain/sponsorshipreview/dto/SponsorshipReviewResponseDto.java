package com.goormthon.hero_home.domain.sponsorshipreview.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class SponsorshipReviewResponseDto {

    @Getter
    @Builder
    public static class SponsorshipReviewInfo {
        Long reviewId;
        String title;
        String content;
        List<String> sponsoershipReviewPhotos;
    }
}
