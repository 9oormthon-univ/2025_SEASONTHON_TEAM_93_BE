package com.goormthon.hero_home.domain.warmemoirreply.controller;

import com.goormthon.hero_home.domain.user.entity.User;
import com.goormthon.hero_home.domain.user.service.UserService;
import com.goormthon.hero_home.domain.warmemoirreply.dto.WarMemoirReplyResponseDto;
import com.goormthon.hero_home.domain.warmemoirreply.service.WarMemoirReplyService;
import com.goormthon.hero_home.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/replies")
@RequiredArgsConstructor
@Tag(name = "MyReply", description = "내 댓글 관리 API")
public class MyReplyController {

    private final WarMemoirReplyService warMemoirReplyService;
    private final UserService userService;
    
    // 허용되는 정렬 필드 목록
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
        "id", "title", "content", "createdAt", "updatedAt"
    );

    @GetMapping("/my")
    @Operation(summary = "내가 작성한 댓글 목록 조회", 
               description = "현재 사용자가 작성한 모든 회고록 댓글을 페이징하여 조회합니다. 댓글이 작성된 회고록 정보도 함께 제공됩니다.")
    public ResponseEntity<ApiResponse<Page<WarMemoirReplyResponseDto>>> getMyReplies(
            @Parameter(description = "페이징 정보")
            @PageableDefault(size = 10)
            @SortDefault.SortDefaults({
                @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            }) Pageable pageable,
            Authentication authentication) {
        
        try {
            // 정렬 파라미터 검증 및 정제
            Pageable validatedPageable = validateAndFixPageable(pageable);
            
            User currentUser = getCurrentUser(authentication);
            Page<WarMemoirReplyResponseDto> myReplies = warMemoirReplyService.getMyReplies(currentUser, validatedPageable);
            
            log.info("사용자 댓글 목록 조회: {} (총 {}개, 페이지: {})", 
                    currentUser.getEmail(), myReplies.getTotalElements(), validatedPageable.getPageNumber());
            
            return ResponseEntity.ok(ApiResponse.onSuccess(myReplies));
            
        } catch (IllegalArgumentException e) {
            log.warn("내 댓글 목록 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.onFailure("INVALID_REQUEST", e.getMessage(), null));
                    
        } catch (Exception e) {
            log.error("내 댓글 목록 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.onFailure("MY_REPLIES_ERROR", "댓글 목록 조회 중 오류가 발생했습니다.", null));
        }
    }

    @GetMapping("/my/count")
    @Operation(summary = "내가 작성한 댓글 개수 조회", 
               description = "현재 사용자가 작성한 전체 댓글 개수를 조회합니다.")
    public ResponseEntity<ApiResponse<Long>> getMyReplyCount(Authentication authentication) {
        
        try {
            User currentUser = getCurrentUser(authentication);
            Long replyCount = warMemoirReplyService.getMyReplyCount(currentUser);
            
            log.debug("사용자 댓글 개수 조회: {} ({}개)", currentUser.getEmail(), replyCount);
            
            return ResponseEntity.ok(ApiResponse.onSuccess(replyCount));
            
        } catch (IllegalArgumentException e) {
            log.warn("내 댓글 개수 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.onFailure("INVALID_REQUEST", e.getMessage(), null));
                    
        } catch (Exception e) {
            log.error("내 댓글 개수 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.onFailure("MY_REPLY_COUNT_ERROR", "댓글 개수 조회 중 오류가 발생했습니다.", null));
        }
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

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
    }
}