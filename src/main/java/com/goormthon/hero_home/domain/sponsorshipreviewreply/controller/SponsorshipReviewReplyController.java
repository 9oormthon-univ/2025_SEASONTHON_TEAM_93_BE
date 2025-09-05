package com.goormthon.hero_home.domain.sponsorshipreviewreply.controller;

import com.goormthon.hero_home.domain.sponsorshipreview.dto.SponsorshipReviewRequestDto;
import com.goormthon.hero_home.domain.sponsorshipreviewreply.dto.SponsorshipReviewReplyResponseDto;
import com.goormthon.hero_home.domain.sponsorshipreviewreply.service.SponsorshipReviewReplyService;
import com.goormthon.hero_home.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sponsorship-review-reply")
@Tag(name = "SponsorshipReviewReply", description = "관리자의 후원 현황 댓글 관리 API")
public class SponsorshipReviewReplyController {

    private final SponsorshipReviewReplyService sponsorshipReviewReplyService;

    @PostMapping("/")
    @Operation(summary = "유저의 후원 현황 댓글 작성", description = "유저의 후원 현황 글 속 댓글 작성 API")
    public ApiResponse<String> registerReply(Authentication authentication,
                                             @RequestBody SponsorshipReviewRequestDto.SponsorshipReviewInfo sponsorshipReviewInfo) {
        sponsorshipReviewReplyService.registerReply(authentication, sponsorshipReviewInfo);
        return ApiResponse.onSuccess("Sponsorship reply registered");
    }

    @DeleteMapping("/{replyId}")
    @Operation(summary = "유저의 후원 현황 댓글 삭제", description = "유저의 후원 현황 글 속 댓글 삭제 API")
    public ApiResponse<String> deleteReply(Authentication authentication,
                                           @PathVariable Long replyId) {
        sponsorshipReviewReplyService.deleteReply(authentication, replyId);
        return ApiResponse.onSuccess("Sponsorship reply deleted");
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "후원 현황 리뷰 속 댓글 리스트", description = "댓글 리스트 API")
    public ApiResponse<List<SponsorshipReviewReplyResponseDto.SponsorshipReviewReplyInfo>> getAllReply(@PathVariable Long reviewId) {
        return ApiResponse.onSuccess(sponsorshipReviewReplyService.getAllReply(reviewId));
    }

    @GetMapping("/my")
    @Operation(summary = "유저의 내 댓글 리스트", description = "유저의 내 댓글 리스트 API")
    public ApiResponse<List<SponsorshipReviewReplyResponseDto.SponsorshipReviewReplyInfo>> getMyReply(Authentication authentication) {
        sponsorshipReviewReplyService.getMyReply(authentication);
        return ApiResponse.onSuccess(sponsorshipReviewReplyService.getMyReply(authentication));
    }
}
