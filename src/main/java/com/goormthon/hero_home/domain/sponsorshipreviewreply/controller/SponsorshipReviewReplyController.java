package com.goormthon.hero_home.domain.sponsorshipreviewreply.controller;

import com.goormthon.hero_home.domain.sponsorshipreview.dto.SponsorshipReviewRequestDto;
import com.goormthon.hero_home.domain.sponsorshipreviewreply.dto.SponsorshipReviewReplyResponseDto;
import com.goormthon.hero_home.domain.sponsorshipreviewreply.service.SponsorshipReviewReplyService;
import com.goormthon.hero_home.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sponsorship-review-reply")
public class SponsorshipReviewReplyController {

    private final SponsorshipReviewReplyService sponsorshipReviewReplyService;

    @PostMapping("/")
    public ApiResponse<String> registerReply(Authentication authentication,
                                             @RequestBody SponsorshipReviewRequestDto.SponsorshipReviewInfo sponsorshipReviewInfo) {
        sponsorshipReviewReplyService.registerReply(authentication, sponsorshipReviewInfo);
        return ApiResponse.onSuccess("Sponsorship reply registered");
    }

    @DeleteMapping("/{replyId]")
    public ApiResponse<String> deleteReply(Authentication authentication,
                                           @PathVariable Long replyId) {
        sponsorshipReviewReplyService.deleteReply(authentication, replyId);
        return ApiResponse.onSuccess("Sponsorship reply deleted");
    }

    @GetMapping("/{reviewId}")
    public ApiResponse<List<SponsorshipReviewReplyResponseDto.SponsorshipReviewReplyInfo>> getAllReply(@PathVariable Long reviewId) {
        return ApiResponse.onSuccess(sponsorshipReviewReplyService.getAllReply(reviewId));
    }

    @GetMapping("/my")
    public ApiResponse<List<SponsorshipReviewReplyResponseDto.SponsorshipReviewReplyInfo>> getMyReply(Authentication authentication) {
        sponsorshipReviewReplyService.getMyReply(authentication);
        return ApiResponse.onSuccess(sponsorshipReviewReplyService.getMyReply(authentication));
    }
}
