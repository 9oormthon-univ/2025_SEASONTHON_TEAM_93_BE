package com.goormthon.hero_home.domain.sponsorshipboard.converter;

import com.goormthon.hero_home.domain.sponsorshipboard.dto.SponsorshipBoardResponseDto;
import com.goormthon.hero_home.domain.sponsorshipboard.entity.SponsorshipBoard;
import com.goormthon.hero_home.domain.sponsorshipboard.entity.SponsorshipBoardPhotos;

import java.util.List;
import java.util.stream.Collectors;

public class SponsorshipBoardConverter {

    public static SponsorshipBoardResponseDto.SponsorshipBoardInfo toSponsorshipBoardInfo(SponsorshipBoard sponsorshipBoard, List<SponsorshipBoardPhotos> photos) {
        return SponsorshipBoardResponseDto.SponsorshipBoardInfo.builder()
                .sponsorshipBoardId(sponsorshipBoard.getId())
                .title(sponsorshipBoard.getTitle())
                .sponsorshipBoardStatus(sponsorshipBoard.getSponsorshipStatus())
                .content(sponsorshipBoard.getContent())
                .currentAmount(sponsorshipBoard.getCurrentAmount())
                .targetAmount(sponsorshipBoard.getTargetAmount())
                .startDate(sponsorshipBoard.getStartDate())
                .endDate(sponsorshipBoard.getEndDate())
                .currentAmount(sponsorshipBoard.getCurrentAmount())
                .sponsorshipBoardPhotos(photos.stream().map(SponsorshipBoardPhotos::getFilePath).collect(Collectors.toList()))
                .build();
    }
}
