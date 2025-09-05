package com.goormthon.hero_home.domain.sponsorshipreviewreply.converter;

import com.goormthon.hero_home.domain.sponsorshipreviewreply.dto.SponsorshipReviewReplyResponseDto;
import com.goormthon.hero_home.domain.sponsorshipreviewreply.entity.SponsorshipReviewReply;

public class SponsorshipReviewReplyConverter {

    public static SponsorshipReviewReplyResponseDto.SponsorshipReviewReplyInfo toReplyInfo(SponsorshipReviewReply sponsorshipReviewReply) {
        return SponsorshipReviewReplyResponseDto.SponsorshipReviewReplyInfo.builder()
                .replyId(sponsorshipReviewReply.getId())
                .title(sponsorshipReviewReply.getTitle())
                .content(sponsorshipReviewReply.getContent())
                .createdAt(sponsorshipReviewReply.getCreatedAt())
                .email(sponsorshipReviewReply.getUser().getEmail())
                .build();
    }
}
