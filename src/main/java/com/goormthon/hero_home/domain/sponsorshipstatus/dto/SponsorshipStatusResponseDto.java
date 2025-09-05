package com.goormthon.hero_home.domain.sponsorshipstatus.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class SponsorshipStatusResponseDto {

    @Getter
    @Builder
    public static class SponsorshipDonationInfo {
        Long id;
        Integer sponsorshipAmount;
        Boolean isApproved;
        String email;
        LocalDate createdAt;
        Long sponsorshipBoardId;
    }
}
