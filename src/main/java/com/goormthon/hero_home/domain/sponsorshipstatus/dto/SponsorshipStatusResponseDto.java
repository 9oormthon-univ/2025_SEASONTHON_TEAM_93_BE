package com.goormthon.hero_home.domain.sponsorshipstatus.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SponsorshipStatusResponseDto {

    @Getter
    @Builder
    public static class SponsorshipDonationInfo {
        Long id;
        Integer sponsorshipAmount;
        Boolean isApproved;
        String email;
        LocalDateTime createdAt;
        Long sponsorshipBoardId;
    }

    @Getter
    @Builder
    public static class SponsorshipProgressInfo {
        Integer targetAmount;
        Integer currentAmount;
    }
}
