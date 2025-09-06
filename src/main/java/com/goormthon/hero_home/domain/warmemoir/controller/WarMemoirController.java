package com.goormthon.hero_home.domain.warmemoir.controller;

import com.goormthon.hero_home.domain.warmemoir.dto.WarMemoirRequestDto;
import com.goormthon.hero_home.domain.warmemoir.dto.WarMemoirResponseDto;
import com.goormthon.hero_home.domain.warmemoir.dto.WarMemoirSummaryDto;
import com.goormthon.hero_home.domain.warmemoir.service.WarMemoirService;
import com.goormthon.hero_home.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/warmemoir")
@RequiredArgsConstructor
@Tag(name = "WarMemoir", description = "참전 회고록 API")
public class WarMemoirController {

    private final WarMemoirService warMemoirService;
    
    // 허용되는 정렬 필드 목록
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
        "id", "title", "image", "createdAt", "updatedAt"
    );

    @GetMapping
    @Operation(summary = "회고록 목록 조회", description = "참전 회고록 목록을 페이징하여 조회합니다.")
    public ResponseEntity<ApiResponse<Page<WarMemoirSummaryDto>>> getAllWarMemoirs(
            @Parameter(description = "검색 키워드 (제목 검색)")
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10)
            @SortDefault.SortDefaults({
                @SortDefault(sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC)
            }) Pageable pageable) {
        
        try {
            // 정렬 파라미터 검증 및 정제
            Pageable validatedPageable = validateAndFixPageable(pageable);
            
            Page<WarMemoirSummaryDto> warMemoirs;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                warMemoirs = warMemoirService.searchWarMemoirs(keyword.trim(), validatedPageable);
                log.info("회고록 검색: '{}', 페이지: {}, 크기: {}", keyword, validatedPageable.getPageNumber(), validatedPageable.getPageSize());
            } else {
                warMemoirs = warMemoirService.getAllWarMemoirs(validatedPageable);
                log.info("회고록 목록 조회: 페이지: {}, 크기: {}", validatedPageable.getPageNumber(), validatedPageable.getPageSize());
            }
            
            return ResponseEntity.ok(ApiResponse.onSuccess(warMemoirs));
            
        } catch (Exception e) {
            log.error("회고록 목록 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.onFailure("WAR_MEMOIR_LIST_ERROR", "회고록 목록 조회 중 오류가 발생했습니다.", null));
        }
    }
    
    private Pageable validateAndFixPageable(Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return pageable;
        }
        
        Sort validatedSort = Sort.by(
            pageable.getSort().stream()
                .filter(order -> ALLOWED_SORT_FIELDS.contains(order.getProperty()))
                .toArray(Sort.Order[]::new)
        );
        
        // 유효한 정렬이 없으면 기본 정렬 적용
        if (validatedSort.isEmpty()) {
            validatedSort = Sort.by(Sort.Direction.DESC, "createdAt");
            log.warn("유효하지 않은 정렬 파라미터가 감지되어 기본 정렬로 대체됨: createdAt,desc");
        }
        
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), validatedSort);
    }

    @GetMapping("/{id}")
    @Operation(summary = "회고록 상세 조회", description = "특정 회고록의 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<WarMemoirResponseDto>> getWarMemoirById(
            @Parameter(description = "회고록 ID")
            @PathVariable Long id) {
        
        try {
            WarMemoirResponseDto warMemoir = warMemoirService.getWarMemoirById(id);
            return ResponseEntity.ok(ApiResponse.onSuccess(warMemoir));
            
        } catch (IllegalArgumentException e) {
            log.warn("회고록 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.onFailure("WAR_MEMOIR_NOT_FOUND", e.getMessage(), null));
                    
        } catch (Exception e) {
            log.error("회고록 상세 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.onFailure("WAR_MEMOIR_GET_ERROR", "회고록 조회 중 오류가 발생했습니다.", null));
        }
    }

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "회고록 생성", description = "새로운 참전 회고록을 생성합니다. (관리자 전용)")
    public ResponseEntity<ApiResponse<WarMemoirResponseDto>> createWarMemoir(
            @Parameter(description = "회고록 정보") 
            @RequestPart("requestDto") @Valid WarMemoirRequestDto requestDto,
            @Parameter(description = "대표 이미지 파일")
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        
        try {
            WarMemoirResponseDto createdWarMemoir = warMemoirService.createWarMemoir(requestDto, imageFile);
            return ResponseEntity.ok(ApiResponse.onSuccess(createdWarMemoir));
            
        } catch (Exception e) {
            log.error("회고록 생성 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.onFailure("WAR_MEMOIR_CREATE_ERROR", "회고록 생성 중 오류가 발생했습니다.", null));
        }
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "회고록 수정", description = "기존 회고록을 수정합니다. (관리자 전용)")
    public ResponseEntity<ApiResponse<WarMemoirResponseDto>> updateWarMemoir(
            @Parameter(description = "회고록 ID")
            @PathVariable Long id,
            @Parameter(description = "회고록 정보") 
            @RequestPart("requestDto") @Valid WarMemoirRequestDto requestDto,
            @Parameter(description = "대표 이미지 파일")
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        
        try {
            WarMemoirResponseDto updatedWarMemoir = warMemoirService.updateWarMemoir(id, requestDto, imageFile);
            return ResponseEntity.ok(ApiResponse.onSuccess(updatedWarMemoir));
            
        } catch (IllegalArgumentException e) {
            log.warn("회고록 수정 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.onFailure("WAR_MEMOIR_NOT_FOUND", e.getMessage(), null));
                    
        } catch (Exception e) {
            log.error("회고록 수정 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.onFailure("WAR_MEMOIR_UPDATE_ERROR", "회고록 수정 중 오류가 발생했습니다.", null));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "회고록 삭제", description = "회고록을 삭제합니다. (관리자 전용)")
    public ResponseEntity<ApiResponse<String>> deleteWarMemoir(
            @Parameter(description = "회고록 ID")
            @PathVariable Long id) {
        
        try {
            warMemoirService.deleteWarMemoir(id);
            return ResponseEntity.ok(ApiResponse.onSuccess("회고록이 삭제되었습니다."));
            
        } catch (IllegalArgumentException e) {
            log.warn("회고록 삭제 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.onFailure("WAR_MEMOIR_NOT_FOUND", e.getMessage(), null));
                    
        } catch (Exception e) {
            log.error("회고록 삭제 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.onFailure("WAR_MEMOIR_DELETE_ERROR", "회고록 삭제 중 오류가 발생했습니다.", null));
        }
    }
}