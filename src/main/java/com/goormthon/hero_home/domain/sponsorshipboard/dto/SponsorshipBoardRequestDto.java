package com.goormthon.hero_home.domain.sponsorshipboard.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SponsorshipBoardRequestDto {

    @Getter
    public static class BoardInfoRequestDto {
        String title;
        String subTitle;
        String content;
        Integer targetAmount;
        LocalDate startDate;
        LocalDate endDate;
    }
}
