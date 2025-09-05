package com.goormthon.hero_home.domain.sponsorshipreview.converter;

import com.goormthon.hero_home.domain.sponsorshipreview.dto.SponsorshipReviewResponseDto;
import com.goormthon.hero_home.domain.sponsorshipreview.entity.SponsorshipReview;
import com.goormthon.hero_home.domain.sponsorshipreview.entity.SponsorshipReviewPhotos;

import java.util.List;
import java.util.stream.Collectors;

public class SponsorshipReviewConverter {

    public static SponsorshipReviewResponseDto.SponsorshipReviewInfo toSponsorshipReviewInfo(SponsorshipReview sponsorshipReview, List<SponsorshipReviewPhotos> photos) {
        return SponsorshipReviewResponseDto.SponsorshipReviewInfo.builder()
                .reviewId(sponsorshipReview.getId())
                .title(sponsorshipReview.getTitle())
                .content(sponsorshipReview.getContent())
                .sponsoershipReviewPhotos(photos.stream().map(SponsorshipReviewPhotos::getFilePath).collect(Collectors.toList()))
                .build();

    }
}
