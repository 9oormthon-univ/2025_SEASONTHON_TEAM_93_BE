package com.goormthon.hero_home.domain.sponsorshipboard.controller;

import com.goormthon.hero_home.domain.sponsorshipboard.dto.SponsorshipBoardRequestDto;
import com.goormthon.hero_home.domain.sponsorshipboard.dto.SponsorshipBoardResponseDto;
import com.goormthon.hero_home.domain.sponsorshipboard.service.SponsorshipBoardService;
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
@RequestMapping("/api/sponsorship-board")
@Tag(name = "SponsorshipBoard", description = "관리자의 후원 글 관리 API")
 public class SponsorshipBoardController {

    private final SponsorshipBoardService sponsorshipBoardService;

    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "관리자의 후원 글 등록", description = "관리자의 후원 글 등록 API")
    public ApiResponse<String> registerBoard(Authentication authentication,
                                             @RequestPart SponsorshipBoardRequestDto.BoardInfoRequestDto boardInfoRequestDto,
                                             @RequestPart(name = "ImageFile", required = false) List<MultipartFile> imgs) {
        sponsorshipBoardService.registerBoard(authentication, boardInfoRequestDto, imgs);
        return ApiResponse.onSuccess("Board registered successfully");
    }

    @GetMapping("/{boardId}")
    @Operation(summary = "후원 글 상세보기", description = "후원 글 상세 API")
    public ApiResponse<SponsorshipBoardResponseDto.SponsorshipBoardInfo> getBoard(@PathVariable Long boardId) {
        return ApiResponse.onSuccess(sponsorshipBoardService.getBoard(boardId));
    }

    @PutMapping(
            value = "/{boardId}",
            consumes = "multipart/form-data")
    @Operation(summary = "관리자의 후원 글 수정하기", description = "관리자의 후원 글 수정 API")
    public ApiResponse<String> updateBoard(Authentication authentication,
                                           @PathVariable Long boardId,
                                           @RequestPart SponsorshipBoardRequestDto.BoardInfoRequestDto boardInfoRequestDto,
                                           @RequestPart(name = "ImageFile", required = false) List<MultipartFile> imgs) {
        sponsorshipBoardService.updateBoard(authentication, boardId, boardInfoRequestDto, imgs);
        return ApiResponse.onSuccess("Board updated successfully");
    }

    @DeleteMapping("/{boardId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "관리자의 후원 글 삭제하기", description = "관리자의 후원 글 삭제 API")
    public ApiResponse<String> deleteBoard(Authentication authentication, @PathVariable Long boardId) {
        sponsorshipBoardService.deleteBoard(authentication, boardId);
        return ApiResponse.onSuccess("Board deleted successfully");
    }

    @GetMapping("/")
    @Operation(summary = "후원 글 리스트", description = "후원 글 리스트 6개씩 보여집니다.")
    public ApiResponse<Page<SponsorshipBoardResponseDto.SponsorshipBoardInfo>> getAllBoards(
            @ParameterObject @PageableDefault(size = 6, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResponse.onSuccess(sponsorshipBoardService.getAllBoards(pageable));
    }
}
