package com.goormthon.hero_home.domain.sponsorshipstatus.controller;

import com.goormthon.hero_home.domain.sponsorshipstatus.dto.SponsorshipStatusRequestDto;
import com.goormthon.hero_home.domain.sponsorshipstatus.dto.SponsorshipStatusResponseDto;
import com.goormthon.hero_home.domain.sponsorshipstatus.service.SponsorshipStatusService;
import com.goormthon.hero_home.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ApiResponse<?> approveDonation(@PathVariable Long statusId, Authentication authentication) {
        sponsorshipStatusService.approveDonation(statusId, authentication);
        return ApiResponse.onSuccess("Approve Donation successful.");
    }

    //후원현황때 목표금액, 모금 금액 랜딩해야함

}
