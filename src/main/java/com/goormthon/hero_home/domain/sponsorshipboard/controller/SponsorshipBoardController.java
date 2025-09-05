package com.goormthon.hero_home.domain.sponsorshipboard.controller;

import com.goormthon.hero_home.domain.sponsorshipboard.dto.SponsorshipBoardRequestDto;
import com.goormthon.hero_home.domain.sponsorshipboard.dto.SponsorshipBoardResponseDto;
import com.goormthon.hero_home.domain.sponsorshipboard.service.SponsorshipBoardService;
import com.goormthon.hero_home.global.ApiResponse;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/sponsorship-board")
public class SponsorshipBoardController {

    private final SponsorshipBoardService sponsorshipBoardService;

    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> registerBoard(Authentication authentication,
                                             @RequestPart SponsorshipBoardRequestDto.BoardInfoRequestDto boardInfoRequestDto,
                                             @RequestPart(name = "ImageFile", required = false) List<MultipartFile> imgs) {
        sponsorshipBoardService.registerBoard(authentication, boardInfoRequestDto, imgs);
        return ApiResponse.onSuccess("Board registered successfully");
    }

    @GetMapping("/{boardId}")
    public ApiResponse<SponsorshipBoardResponseDto.SponsorshipBoardInfo> getBoard(@PathVariable Long boardId) {
        return ApiResponse.onSuccess(sponsorshipBoardService.getBoard(boardId));
    }

    @PutMapping("/{boardId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> updateBoard(Authentication authentication,
                                           @PathVariable Long boardId,
                                           @RequestPart SponsorshipBoardRequestDto.BoardInfoRequestDto boardInfoRequestDto,
                                           @RequestPart(name = "ImageFile", required = false) List<MultipartFile> imgs) {
        sponsorshipBoardService.updateBoard(authentication, boardId, boardInfoRequestDto, imgs);
        return ApiResponse.onSuccess("Board updated successfully");
    }

    @DeleteMapping("/{boardId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteBoard(Authentication authentication, @PathVariable Long boardId) {
        sponsorshipBoardService.deleteBoard(authentication, boardId);
        return ApiResponse.onSuccess("Board deleted successfully");
    }

    @GetMapping("/")
    public ApiResponse<Page<SponsorshipBoardResponseDto.SponsorshipBoardInfo>> getAllBoards(
            @PageableDefault(size = 6, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResponse.onSuccess(sponsorshipBoardService.getAllBoards(pageable));
    }
}
