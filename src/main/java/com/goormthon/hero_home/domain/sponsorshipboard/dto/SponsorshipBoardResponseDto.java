package com.goormthon.hero_home.domain.sponsorshipboard.dto;

import com.goormthon.hero_home.domain.sponsorshipboard.entity.SponsorshipBoardStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class SponsorshipBoardResponseDto {

    @Getter
    @Builder
    public static class SponsorshipBoardInfo {
        Long sponsorshipBoardId;
        String title;
        String subTitle;
        String content;
        Integer targetAmount;
        LocalDateTime startDate;
        LocalDateTime endDate;
        Integer currentAmount;
        SponsorshipBoardStatus sponsorshipBoardStatus;
        List<String> sponsorshipBoardPhotos;
    }
}
