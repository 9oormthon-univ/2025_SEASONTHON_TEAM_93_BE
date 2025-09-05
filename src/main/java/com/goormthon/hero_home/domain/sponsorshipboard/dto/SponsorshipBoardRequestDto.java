package com.goormthon.hero_home.domain.sponsorshipboard.dto;

import lombok.Getter;

import java.time.LocalDateTime;

public class SponsorshipBoardRequestDto {

    @Getter
    public static class BoardInfoRequestDto {
        String title;
        String subTitle;
        String content;
        Integer targetAmount;
        LocalDateTime startDate;
        LocalDateTime endDate;
    }
}
