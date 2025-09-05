package com.goormthon.hero_home.domain.sponsorshipstatus.converter;

import com.goormthon.hero_home.domain.sponsorshipstatus.dto.SponsorshipStatusResponseDto;
import com.goormthon.hero_home.domain.sponsorshipstatus.entity.SponsorshipStatus;

public class SponsorshipStatusConverter {

    public static SponsorshipStatusResponseDto.SponsorshipDonationInfo toSponsorshipStatusInfo(SponsorshipStatus sponsorshipStatus) {
        return SponsorshipStatusResponseDto.SponsorshipDonationInfo.builder()
                .id(sponsorshipStatus.getId())
                .sponsorshipAmount(sponsorshipStatus.getSponsorshipAmount())
                .sponsorshipBoardId(sponsorshipStatus.getSponsorshipBoard().getId())
                .email(sponsorshipStatus.getUser().getEmail())
                .createdAt(sponsorshipStatus.getCreatedAt())
                .isApproved(sponsorshipStatus.getIsApproved())
                .build();
    }
}
