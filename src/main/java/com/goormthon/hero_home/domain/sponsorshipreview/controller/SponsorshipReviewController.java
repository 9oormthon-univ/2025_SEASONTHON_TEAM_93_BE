package com.goormthon.hero_home.domain.sponsorshipreview.controller;

import com.goormthon.hero_home.domain.sponsorshipreview.dto.SponsorshipReviewRequestDto;
import com.goormthon.hero_home.domain.sponsorshipreview.dto.SponsorshipReviewResponseDto;
import com.goormthon.hero_home.domain.sponsorshipreview.service.SponsorshipReviewService;
import com.goormthon.hero_home.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/sponsorship-review")
@Tag(name = "SponsorshipReview", description = "관리자의 후원 현황 리뷰 관리 API")
public class SponsorshipReviewController {

    private final SponsorshipReviewService sponsorshipReviewService;

    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "관리자의 후원 현황 리뷰 작성하기", description = "관리자의 후원 현황 리뷰 작성 API")
    public ApiResponse<String> registerReview(Authentication authentication,
                                              @RequestPart SponsorshipReviewRequestDto.SponsorshipReviewInfo sponsorshipReviewInfoDto,
                                              @RequestPart(name = "ImageFile", required = false) List<MultipartFile> imgs) {
        sponsorshipReviewService.registerReview(authentication, sponsorshipReviewInfoDto, imgs);
        return ApiResponse.onSuccess("review registered successfully");
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "후원 현황 리뷰 상세보기", description = "후원 현황 상세 API")
    public ApiResponse<SponsorshipReviewResponseDto.SponsorshipReviewInfo> getReview(@PathVariable Long reviewId) {
        return ApiResponse.onSuccess(sponsorshipReviewService.getReview(reviewId));
    }

    @PutMapping(
            value = "/{reviewId}",
            consumes = "multipart/form-data")
    @Operation(summary = "관리자의 후원 현황 리뷰 수정하기", description = "관리자의 후원 현황 리뷰 수정 API")
    public ApiResponse<String> updateReview(Authentication authentication,
                                            @PathVariable Long reviewId,
                                            @RequestPart SponsorshipReviewRequestDto.SponsorshipReviewUpdate sponsorshipReviewUpdate,
                                            @RequestPart(name = "ImageFile", required = false) List<MultipartFile> imgs) {
        sponsorshipReviewService.updateReview(authentication, reviewId, sponsorshipReviewUpdate, imgs);
        return ApiResponse.onSuccess("review updated successfully");
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "관리자의 후원 현황 리뷰 삭제하기", description = "관리자의 후원 현황 리뷰 삭제 API")
    public ApiResponse<String> deleteReview(Authentication authentication,
                                            @PathVariable Long reviewId) {
        sponsorshipReviewService.deleteReview(authentication, reviewId);
        return ApiResponse.onSuccess("review deleted successfully");
    }

    @GetMapping("/")
    @Operation(summary = "후원 현황 리뷰 리스트", description = "후원 현황 리뷰 6개씩 리스트")
    public ApiResponse<Page<SponsorshipReviewResponseDto.SponsorshipReviewInfo>> getAllReviews(
            @ParameterObject @PageableDefault(size = 6, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResponse.onSuccess(sponsorshipReviewService.getAllReviews(pageable));
    }
}
