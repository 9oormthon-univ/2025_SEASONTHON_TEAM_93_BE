package com.goormthon.hero_home.domain.sponsorshipstatus.controller;

import com.goormthon.hero_home.domain.sponsorshipstatus.dto.SponsorshipStatusRequestDto;
import com.goormthon.hero_home.domain.sponsorshipstatus.dto.SponsorshipStatusResponseDto;
import com.goormthon.hero_home.domain.sponsorshipstatus.service.SponsorshipStatusService;
import com.goormthon.hero_home.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sponsorship-status")
public class SponsorshipStatusController {

    private final SponsorshipStatusService sponsorshipStatusService;

    // 유저의 후원 금액 등록
    @PostMapping("/{boardId}/donate")
    public ApiResponse<String> donate(Authentication authentication,
                                      @PathVariable Long boardId,
                                      @RequestBody SponsorshipStatusRequestDto.SponsorshipDonationInfo donationInfo) {
        sponsorshipStatusService.donate(authentication, boardId, donationInfo);
        return ApiResponse.onSuccess("Donation successful.");
    }

    //후기 승인 대기 리스트 끌어오기
    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<SponsorshipStatusResponseDto.SponsorshipDonationInfo>> getAllSponsorshipStatus(Authentication authentication) {
        return ApiResponse.onSuccess(sponsorshipStatusService.getAllSponsorshipStatus(authentication));
    }

    //관리자의 후원 승인
    @PostMapping("/{statudId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> approveDonation(@PathVariable Long statuId, Authentication authentication) {
        sponsorshipStatusService.approveDonation(statuId, authentication);
        return ApiResponse.onSuccess("Approve Donation successful.");
    }

    //후원현황때 목표금액, 모금 금액 랜딩해야함

}
