package com.goormthon.hero_home.domain.sponsorshipstatus.controller;

import com.goormthon.hero_home.domain.sponsorshipstatus.dto.SponsorshipStatusRequestDto;
import com.goormthon.hero_home.domain.sponsorshipstatus.dto.SponsorshipStatusResponseDto;
import com.goormthon.hero_home.domain.sponsorshipstatus.service.SponsorshipStatusService;
import com.goormthon.hero_home.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sponsorship-status")
@Tag(name = "SponsorshipStatus", description = "후원 현황 및 후원 승인 관련 API")
public class SponsorshipStatusController {

    private final SponsorshipStatusService sponsorshipStatusService;

    @PostMapping("/{boardId}/donate")
    @Operation(summary = "유저의 후원 금액 등록", description = "유저의 후원 금액 등록 API")
    public ApiResponse<String> donate(Authentication authentication,
                                      @PathVariable Long boardId,
                                      @RequestBody SponsorshipStatusRequestDto.SponsorshipDonationInfo donationInfo) {
        sponsorshipStatusService.donate(authentication, boardId, donationInfo);
        return ApiResponse.onSuccess("Donation successful.");
    }

    @GetMapping("/")
    @Operation(summary = "관리자의 후원 승인 리스트", description = "관리자의 후원 승인 리스트 API")
    public ApiResponse<List<SponsorshipStatusResponseDto.SponsorshipDonationInfo>> getAllSponsorshipStatus(Authentication authentication) {
        return ApiResponse.onSuccess(sponsorshipStatusService.getAllSponsorshipStatus(authentication));
    }

    @PostMapping("/{statusId}")
    @Operation(summary = "관리자의 후원 승인", description = "관리자의 후원 승인 API")
    public ApiResponse<String> approveDonation(@PathVariable Long statusId, Authentication authentication) {
        sponsorshipStatusService.approveDonation(statusId, authentication);
        return ApiResponse.onSuccess("Approve Donation successful.");
    }

    @GetMapping("/user")
    @Operation(summary = "유저의 후원 신청 리스트", description = "관리자의 후원완료 신청 리스트 API")
    public ApiResponse<List<SponsorshipStatusResponseDto.SponsorshipDonationInfo>> getUserDonation(Authentication authentication) {
        return ApiResponse.onSuccess(sponsorshipStatusService.getUserDonation(authentication));
    }

    @GetMapping("/{boardId}")
    @Operation(summary = "후원현황화면 랜딩때 목표금액, 모금금액 반환", description = "후원현황 랜딩때 목표금액, 모금금액 반환 API")
    public ApiResponse<SponsorshipStatusResponseDto.SponsorshipProgressInfo> getSponsorshipProgress(@PathVariable Long boardId) {
        return ApiResponse.onSuccess(sponsorshipStatusService.getSponsorshipProgress(boardId));
    }
}
