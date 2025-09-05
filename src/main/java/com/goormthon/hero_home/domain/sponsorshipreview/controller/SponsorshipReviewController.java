package com.goormthon.hero_home.domain.sponsorshipreview.controller;

import com.goormthon.hero_home.domain.sponsorshipreview.dto.SponsorshipReviewRequestDto;
import com.goormthon.hero_home.domain.sponsorshipreview.dto.SponsorshipReviewResponseDto;
import com.goormthon.hero_home.domain.sponsorshipreview.service.SponsorshipReviewService;
import com.goormthon.hero_home.global.ApiResponse;
import lombok.RequiredArgsConstructor;
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
public class SponsorshipReviewController {

    private final SponsorshipReviewService sponsorshipReviewService;

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> registerReview(Authentication authentication,
                                              @RequestPart SponsorshipReviewRequestDto.SponsorshipReviewInfo sponsorshipReviewInfoDto,
                                              @RequestPart(name = "ImageFile", required = false) List<MultipartFile> imgs) {
        sponsorshipReviewService.registerReview(authentication, sponsorshipReviewInfoDto, imgs);
        return ApiResponse.onSuccess("review registered successfully");
    }

    @GetMapping("/{boardId}")
    public ApiResponse<SponsorshipReviewResponseDto.SponsorshipReviewInfo> getReview(@PathVariable Long boardId) {
        return ApiResponse.onSuccess(sponsorshipReviewService.getReview(boardId));
    }

    @PutMapping(
            value = "/{reviewId}",
            consumes = "multipart/form-data"
    )    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> updateReview(Authentication authentication,
                                            @PathVariable Long reviewId,
                                            @RequestPart SponsorshipReviewRequestDto.SponsorshipReviewInfo sponsorshipReviewInfoDto,
                                            @RequestPart(name = "ImageFile", required = false) List<MultipartFile> imgs) {
        sponsorshipReviewService.updateReview(authentication, reviewId, sponsorshipReviewInfoDto, imgs);
        return ApiResponse.onSuccess("review updated successfully");
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteReview(@PathVariable Long reviewId) {
        sponsorshipReviewService.deleteReview(reviewId);
        return ApiResponse.onSuccess("review deleted successfully");
    }

    @GetMapping("/")
    public ApiResponse<?> getAllReviews(
            @PageableDefault(size = 6, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResponse.onSuccess(sponsorshipReviewService.getAllReviews(pageable));
    }
}
