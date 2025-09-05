package com.goormthon.hero_home.domain.warmemoirreply.service;

import com.goormthon.hero_home.domain.user.entity.User;
import com.goormthon.hero_home.domain.warmemoir.entity.WarMemoir;
import com.goormthon.hero_home.domain.warmemoir.repository.WarMemoirRepository;
import com.goormthon.hero_home.domain.warmemoirreply.dto.WarMemoirReplyRequestDto;
import com.goormthon.hero_home.domain.warmemoirreply.dto.WarMemoirReplyResponseDto;
import com.goormthon.hero_home.domain.warmemoirreply.entity.WarMemoirReply;
import com.goormthon.hero_home.domain.warmemoirreply.repository.WarMemoirReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarMemoirReplyService {

    private final WarMemoirReplyRepository warMemoirReplyRepository;
    private final WarMemoirRepository warMemoirRepository;

    public Page<WarMemoirReplyResponseDto> getRepliesByWarMemoirId(Long warMemoirId, Pageable pageable) {
        // 회고록 존재 확인
        if (!warMemoirRepository.existsById(warMemoirId)) {
            throw new IllegalArgumentException("회고록을 찾을 수 없습니다: " + warMemoirId);
        }
        
        Page<WarMemoirReply> replies = warMemoirReplyRepository.findByWarMemoirId(warMemoirId, pageable);
        
        return replies.map(WarMemoirReplyResponseDto::from);
    }

    public WarMemoirReplyResponseDto getReplyById(Long replyId) {
        WarMemoirReply reply = warMemoirReplyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다: " + replyId));
        
        return WarMemoirReplyResponseDto.from(reply);
    }

    @Transactional
    public WarMemoirReplyResponseDto createReply(Long warMemoirId, WarMemoirReplyRequestDto requestDto, User user) {
        WarMemoir warMemoir = warMemoirRepository.findById(warMemoirId)
                .orElseThrow(() -> new IllegalArgumentException("회고록을 찾을 수 없습니다: " + warMemoirId));
        
        WarMemoirReply reply = requestDto.toEntity(warMemoir, user);
        WarMemoirReply savedReply = warMemoirReplyRepository.save(reply);
        
        log.info("회고록 댓글 작성: {} (회고록 ID: {}, 사용자: {})", 
                savedReply.getTitle(), warMemoirId, user.getEmail());
        
        return WarMemoirReplyResponseDto.from(savedReply);
    }

    @Transactional
    public WarMemoirReplyResponseDto updateReply(Long replyId, WarMemoirReplyRequestDto requestDto, User user) {
        WarMemoirReply reply = warMemoirReplyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다: " + replyId));
        
        // 작성자 권한 확인
        if (!reply.isAuthor(user)) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }
        
        reply.updateReply(requestDto.getTitle(), requestDto.getContent());
        
        log.info("회고록 댓글 수정: {} (ID: {}, 사용자: {})", 
                reply.getTitle(), replyId, user.getEmail());
        
        return WarMemoirReplyResponseDto.from(reply);
    }

    @Transactional
    public void deleteReply(Long replyId, User user) {
        WarMemoirReply reply = warMemoirReplyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다: " + replyId));
        
        // 작성자 권한 확인
        if (!reply.isAuthor(user)) {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
        }
        
        log.info("회고록 댓글 삭제: {} (ID: {}, 사용자: {})", 
                reply.getTitle(), replyId, user.getEmail());
        
        warMemoirReplyRepository.delete(reply);
    }

    public Long getReplyCountByWarMemoirId(Long warMemoirId) {
        return warMemoirReplyRepository.countByWarMemoirId(warMemoirId);
    }

    public boolean existsById(Long replyId) {
        return warMemoirReplyRepository.existsById(replyId);
    }
}