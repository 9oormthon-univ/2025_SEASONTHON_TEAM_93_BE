package com.goormthon.hero_home.domain.sponsorshipstatus.dto;

import lombok.Getter;

public class SponsorshipStatusRequestDto {

    @Getter
    public static class SponsorshipDonationInfo {
        Integer amount;
    }
}
