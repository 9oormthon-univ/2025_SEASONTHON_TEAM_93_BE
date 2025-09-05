package com.goormthon.hero_home.domain.warmemoirreply.controller;

import com.goormthon.hero_home.domain.user.entity.User;
import com.goormthon.hero_home.domain.user.service.UserService;
import com.goormthon.hero_home.domain.warmemoirreply.dto.WarMemoirReplyRequestDto;
import com.goormthon.hero_home.domain.warmemoirreply.dto.WarMemoirReplyResponseDto;
import com.goormthon.hero_home.domain.warmemoirreply.service.WarMemoirReplyService;
import com.goormthon.hero_home.global.ApiResponse;
import com.goormthon.hero_home.global.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/warmemoir/{warMemoirId}/replies")
@RequiredArgsConstructor
@Tag(name = "WarMemoirReply", description = "회고록 댓글 API")
public class WarMemoirReplyController {

    private final WarMemoirReplyService warMemoirReplyService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    
    // 허용되는 정렬 필드 목록
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
        "id", "title", "content", "createdAt", "updatedAt"
    );

    @GetMapping
    @Operation(summary = "댓글 목록 조회", description = "특정 회고록의 댓글 목록을 페이징하여 조회합니다.")
    public ResponseEntity<ApiResponse<Page<WarMemoirReplyResponseDto>>> getReplies(
            @Parameter(description = "회고록 ID")
            @PathVariable Long warMemoirId,
            @PageableDefault(size = 20)
            @SortDefault.SortDefaults({
                @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            }) Pageable pageable) {
        
        try {
            // 정렬 파라미터 검증 및 정제
            Pageable validatedPageable = validateAndFixPageable(pageable);
            
            Page<WarMemoirReplyResponseDto> replies = warMemoirReplyService.getRepliesByWarMemoirId(warMemoirId, validatedPageable);
            log.info("회고록 댓글 목록 조회: 회고록 ID: {}, 페이지: {}", warMemoirId, validatedPageable.getPageNumber());
            
            return ResponseEntity.ok(ApiResponse.onSuccess(replies));
            
        } catch (IllegalArgumentException e) {
            log.warn("댓글 목록 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.onFailure("WAR_MEMOIR_NOT_FOUND", e.getMessage(), null));
                    
        } catch (Exception e) {
            log.error("댓글 목록 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.onFailure("REPLY_LIST_ERROR", "댓글 목록 조회 중 오류가 발생했습니다.", null));
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

    @GetMapping("/{replyId}")
    @Operation(summary = "댓글 상세 조회", description = "특정 댓글의 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<WarMemoirReplyResponseDto>> getReply(
            @Parameter(description = "회고록 ID")
            @PathVariable Long warMemoirId,
            @Parameter(description = "댓글 ID")
            @PathVariable Long replyId) {
        
        try {
            WarMemoirReplyResponseDto reply = warMemoirReplyService.getReplyById(replyId);
            return ResponseEntity.ok(ApiResponse.onSuccess(reply));
            
        } catch (IllegalArgumentException e) {
            log.warn("댓글 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.onFailure("REPLY_NOT_FOUND", e.getMessage(), null));
                    
        } catch (Exception e) {
            log.error("댓글 상세 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.onFailure("REPLY_GET_ERROR", "댓글 조회 중 오류가 발생했습니다.", null));
        }
    }

    @PostMapping
    @Operation(summary = "댓글 작성", description = "회고록에 댓글을 작성합니다. (인증 필요)")
    public ResponseEntity<ApiResponse<WarMemoirReplyResponseDto>> createReply(
            @Parameter(description = "회고록 ID")
            @PathVariable Long warMemoirId,
            @Valid @RequestBody WarMemoirReplyRequestDto requestDto,
            HttpServletRequest request) {
        
        try {
            User user = getCurrentUser(request);
            WarMemoirReplyResponseDto createdReply = warMemoirReplyService.createReply(warMemoirId, requestDto, user);
            
            return ResponseEntity.ok(ApiResponse.onSuccess(createdReply));
            
        } catch (IllegalArgumentException e) {
            log.warn("댓글 작성 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.onFailure("INVALID_REQUEST", e.getMessage(), null));
                    
        } catch (Exception e) {
            log.error("댓글 작성 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.onFailure("REPLY_CREATE_ERROR", "댓글 작성 중 오류가 발생했습니다.", null));
        }
    }

    @PutMapping("/{replyId}")
    @Operation(summary = "댓글 수정", description = "본인이 작성한 댓글을 수정합니다. (인증 필요)")
    public ResponseEntity<ApiResponse<WarMemoirReplyResponseDto>> updateReply(
            @Parameter(description = "회고록 ID")
            @PathVariable Long warMemoirId,
            @Parameter(description = "댓글 ID")
            @PathVariable Long replyId,
            @Valid @RequestBody WarMemoirReplyRequestDto requestDto,
            HttpServletRequest request) {
        
        try {
            User user = getCurrentUser(request);
            WarMemoirReplyResponseDto updatedReply = warMemoirReplyService.updateReply(replyId, requestDto, user);
            
            return ResponseEntity.ok(ApiResponse.onSuccess(updatedReply));
            
        } catch (IllegalArgumentException e) {
            log.warn("댓글 수정 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.onFailure("INVALID_REQUEST", e.getMessage(), null));
                    
        } catch (Exception e) {
            log.error("댓글 수정 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.onFailure("REPLY_UPDATE_ERROR", "댓글 수정 중 오류가 발생했습니다.", null));
        }
    }

    @DeleteMapping("/{replyId}")
    @Operation(summary = "댓글 삭제", description = "본인이 작성한 댓글을 삭제합니다. (인증 필요)")
    public ResponseEntity<ApiResponse<String>> deleteReply(
            @Parameter(description = "회고록 ID")
            @PathVariable Long warMemoirId,
            @Parameter(description = "댓글 ID")
            @PathVariable Long replyId,
            HttpServletRequest request) {
        
        try {
            User user = getCurrentUser(request);
            warMemoirReplyService.deleteReply(replyId, user);
            
            return ResponseEntity.ok(ApiResponse.onSuccess("댓글이 삭제되었습니다."));
            
        } catch (IllegalArgumentException e) {
            log.warn("댓글 삭제 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.onFailure("INVALID_REQUEST", e.getMessage(), null));
                    
        } catch (Exception e) {
            log.error("댓글 삭제 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.onFailure("REPLY_DELETE_ERROR", "댓글 삭제 중 오류가 발생했습니다.", null));
        }
    }

    private User getCurrentUser(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        
        String email = jwtTokenProvider.getEmailFromToken(token);
        return userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}