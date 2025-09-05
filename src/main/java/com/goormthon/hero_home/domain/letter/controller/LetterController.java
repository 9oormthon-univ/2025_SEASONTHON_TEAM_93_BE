package com.goormthon.hero_home.domain.letter.controller;

import com.goormthon.hero_home.domain.letter.dto.LetterRequestDto;
import com.goormthon.hero_home.domain.letter.dto.LetterResponseDto;
import com.goormthon.hero_home.domain.letter.dto.LetterSummaryDto;
import com.goormthon.hero_home.domain.letter.dto.LetterCountDto;
import com.goormthon.hero_home.domain.letter.service.LetterService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/letters")
@RequiredArgsConstructor
@Tag(name = "Letter", description = "편지 API")
public class LetterController {

    private final LetterService letterService;
    
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
        "id", "title", "isCompleted", "createdAt", "updatedAt"
    );

    @GetMapping
    @Operation(summary = "편지 목록 조회", description = "편지 목록을 페이징하여 조회합니다. 관리자는 모든 편지를, 일반 사용자는 본인의 편지만 조회할 수 있습니다.")
    public ResponseEntity<ApiResponse<Page<LetterSummaryDto>>> getAllLetters(
            @Parameter(description = "검색 키워드 (제목 또는 내용 검색)")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "완료 여부 필터 (true: 완료된 편지만, false: 미완료 편지만, null: 전체)")
            @RequestParam(required = false) Boolean isCompleted,
            @PageableDefault(size = 10)
            @SortDefault.SortDefaults({
                @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            }) Pageable pageable,
            Authentication authentication) {
        
        Pageable validatedPageable = validateAndFixPageable(pageable);
        
        Page<LetterSummaryDto> letters = letterService.getAllLetters(
                keyword, isCompleted, validatedPageable, authentication.getName());
        
        return ResponseEntity.ok(ApiResponse.onSuccess(letters));
    }

    @GetMapping("/{letterId}")
    @Operation(summary = "편지 상세 조회", description = "특정 편지의 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<LetterResponseDto>> getLetterById(
            @Parameter(description = "편지 ID")
            @PathVariable Long letterId,
            Authentication authentication) {
        
        LetterResponseDto letter = letterService.getLetterById(letterId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.onSuccess(letter));
    }

    @PostMapping
    @Operation(summary = "편지 생성", description = "새로운 편지를 생성합니다.")
    public ResponseEntity<ApiResponse<LetterResponseDto>> createLetter(
            @Parameter(description = "편지 생성 요청 정보")
            @Valid @RequestBody LetterRequestDto requestDto,
            Authentication authentication) {
        
        LetterResponseDto createdLetter = letterService.createLetter(requestDto, authentication.getName());
        return ResponseEntity.ok(ApiResponse.onSuccess(createdLetter));
    }

    @PutMapping("/{letterId}")
    @Operation(summary = "편지 수정", description = "기존 편지를 수정합니다. 작성자 본인 또는 관리자만 수정 가능합니다.")
    public ResponseEntity<ApiResponse<LetterResponseDto>> updateLetter(
            @Parameter(description = "편지 ID")
            @PathVariable Long letterId,
            @Parameter(description = "편지 수정 요청 정보")
            @Valid @RequestBody LetterRequestDto requestDto,
            Authentication authentication) {
        
        LetterResponseDto updatedLetter = letterService.updateLetter(letterId, requestDto, authentication.getName());
        return ResponseEntity.ok(ApiResponse.onSuccess(updatedLetter));
    }

    @DeleteMapping("/{letterId}")
    @Operation(summary = "편지 삭제", description = "편지를 삭제합니다. 작성자 본인 또는 관리자만 삭제 가능합니다.")
    public ResponseEntity<ApiResponse<String>> deleteLetter(
            @Parameter(description = "편지 ID")
            @PathVariable Long letterId,
            Authentication authentication) {
        
        letterService.deleteLetter(letterId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.onSuccess("편지가 성공적으로 삭제되었습니다."));
    }

    @PatchMapping("/{letterId}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "편지 완료 상태 토글", description = "편지의 완료 상태를 전환합니다. 관리자만 사용 가능합니다.")
    public ResponseEntity<ApiResponse<LetterResponseDto>> toggleLetterCompleted(
            @Parameter(description = "편지 ID")
            @PathVariable Long letterId,
            Authentication authentication) {
        
        LetterResponseDto letter = letterService.toggleLetterCompleted(letterId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.onSuccess(letter));
    }

    @GetMapping("/warmemoir/{warMemoirId}/count")
    @Operation(summary = "회고록별 편지 개수 조회", description = "특정 회고록에 작성된 편지 개수를 조회합니다. 전체 개수, 완료된 개수, 미완료 개수를 제공합니다.")
    public ResponseEntity<ApiResponse<LetterCountDto>> getLetterCountByWarMemoir(
            @Parameter(description = "회고록 ID")
            @PathVariable Long warMemoirId) {
        
        LetterCountDto letterCount = letterService.getLetterCountByWarMemoir(warMemoirId);
        return ResponseEntity.ok(ApiResponse.onSuccess(letterCount));
    }

    private Pageable validateAndFixPageable(Pageable pageable) {
        Sort validatedSort = Sort.unsorted();
        
        if (pageable.getSort().isSorted()) {
            Sort.Order[] validOrders = pageable.getSort().stream()
                    .filter(order -> ALLOWED_SORT_FIELDS.contains(order.getProperty()))
                    .toArray(Sort.Order[]::new);
            
            if (validOrders.length > 0) {
                validatedSort = Sort.by(validOrders);
            } else {
                validatedSort = Sort.by(Sort.Direction.DESC, "createdAt");
                log.warn("유효하지 않은 정렬 필드가 포함되어 기본 정렬(createdAt DESC)로 변경됨. 허용된 필드: {}", 
                        String.join(", ", ALLOWED_SORT_FIELDS));
            }
        }
        
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), validatedSort);
    }
}