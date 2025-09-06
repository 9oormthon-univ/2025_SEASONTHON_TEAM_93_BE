package com.goormthon.hero_home.domain.warmemoir.service;

import com.goormthon.hero_home.domain.warmemoir.dto.WarMemoirRequestDto;
import com.goormthon.hero_home.domain.warmemoir.dto.WarMemoirResponseDto;
import com.goormthon.hero_home.domain.warmemoir.dto.WarMemoirSummaryDto;
import com.goormthon.hero_home.domain.warmemoir.entity.SubWarMemoir;
import com.goormthon.hero_home.domain.warmemoir.entity.WarMemoir;
import com.goormthon.hero_home.domain.warmemoir.repository.SubWarMemoirRepository;
import com.goormthon.hero_home.domain.warmemoir.repository.WarMemoirRepository;
import com.goormthon.hero_home.domain.warmemoirreply.repository.WarMemoirReplyRepository;
import com.goormthon.hero_home.global.aws.AwsS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarMemoirService {

    private final WarMemoirRepository warMemoirRepository;
    private final SubWarMemoirRepository subWarMemoirRepository;
    private final WarMemoirReplyRepository warMemoirReplyRepository;
    private final AwsS3Service awsS3Service;

    public Page<WarMemoirSummaryDto> getAllWarMemoirs(Pageable pageable) {
        Page<WarMemoir> warMemoirs = warMemoirRepository.findAll(pageable);
        
        return warMemoirs.map(warMemoir -> {
            Long replyCount = warMemoirReplyRepository.countByWarMemoir(warMemoir);
            return WarMemoirSummaryDto.from(warMemoir, replyCount);
        });
    }

    public Page<WarMemoirSummaryDto> searchWarMemoirs(String keyword, Pageable pageable) {
        Page<WarMemoir> warMemoirs = warMemoirRepository.findByTitleContaining(keyword, pageable);
        
        return warMemoirs.map(warMemoir -> {
            Long replyCount = warMemoirReplyRepository.countByWarMemoir(warMemoir);
            return WarMemoirSummaryDto.from(warMemoir, replyCount);
        });
    }

    public WarMemoirResponseDto getWarMemoirById(Long id) {
        WarMemoir warMemoir = warMemoirRepository.findByIdWithSubMemoirs(id)
                .orElseThrow(() -> new IllegalArgumentException("회고록을 찾을 수 없습니다: " + id));
        
        Long replyCount = warMemoirReplyRepository.countByWarMemoir(warMemoir);
        
        log.info("회고록 조회: {} (ID: {})", warMemoir.getTitle(), id);
        
        return WarMemoirResponseDto.from(warMemoir, replyCount);
    }

    @Transactional
    public WarMemoirResponseDto createWarMemoir(WarMemoirRequestDto requestDto, MultipartFile imageFile) {
        // 이미지 파일이 있으면 S3에 업로드
        String imageUrl = requestDto.getImage(); // 기본값: DTO에서 제공된 URL
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = awsS3Service.uploadFile(imageFile);
            log.info("회고록 이미지 업로드 완료: {}", imageUrl);
        }
        
        // 회고록 생성 (업로드된 이미지 URL 사용)
        WarMemoir warMemoir = WarMemoir.builder()
                .title(requestDto.getTitle())
                .image(imageUrl)
                .build();
        WarMemoir savedWarMemoir = warMemoirRepository.save(warMemoir);
        
        // 섹션들 생성
        List<SubWarMemoir> subWarMemoirs = requestDto.toSubWarMemoirEntities(savedWarMemoir);
        subWarMemoirRepository.saveAll(subWarMemoirs);
        
        log.info("회고록 생성: {} (ID: {})", savedWarMemoir.getTitle(), savedWarMemoir.getId());
        
        // 섹션과 함께 다시 조회
        WarMemoir warMemoirWithSections = warMemoirRepository.findByIdWithSubMemoirs(savedWarMemoir.getId())
                .orElseThrow(() -> new IllegalStateException("생성된 회고록을 찾을 수 없습니다."));
        
        return WarMemoirResponseDto.from(warMemoirWithSections, 0L);
    }

    @Transactional
    public WarMemoirResponseDto updateWarMemoir(Long id, WarMemoirRequestDto requestDto, MultipartFile imageFile) {
        WarMemoir warMemoir = warMemoirRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회고록을 찾을 수 없습니다: " + id));
        
        // 이미지 처리 - 새 파일이 업로드되면 S3에 업로드
        String imageUrl = requestDto.getImage(); // 기본값: DTO에서 제공된 URL
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = awsS3Service.uploadFile(imageFile);
            log.info("회고록 이미지 업데이트 완료: {}", imageUrl);
        } else if (imageUrl == null) {
            // 새 파일도 없고 DTO에 URL도 없으면 기존 이미지 유지
            imageUrl = warMemoir.getImage();
        }
        
        // 회고록 기본 정보 수정
        warMemoir.updateMemoir(requestDto.getTitle(), imageUrl);
        
        // 기존 섹션들 삭제
        subWarMemoirRepository.deleteByWarMemoir(warMemoir);
        
        // 새로운 섹션들 생성
        List<SubWarMemoir> newSubWarMemoirs = requestDto.toSubWarMemoirEntities(warMemoir);
        subWarMemoirRepository.saveAll(newSubWarMemoirs);
        
        log.info("회고록 수정: {} (ID: {})", warMemoir.getTitle(), id);
        
        // 수정된 회고록과 섹션들 조회
        WarMemoir updatedWarMemoir = warMemoirRepository.findByIdWithSubMemoirs(id)
                .orElseThrow(() -> new IllegalStateException("수정된 회고록을 찾을 수 없습니다."));
        
        Long replyCount = warMemoirReplyRepository.countByWarMemoir(updatedWarMemoir);
        
        return WarMemoirResponseDto.from(updatedWarMemoir, replyCount);
    }

    @Transactional
    public void deleteWarMemoir(Long id) {
        WarMemoir warMemoir = warMemoirRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회고록을 찾을 수 없습니다: " + id));
        
        log.info("회고록 삭제: {} (ID: {})", warMemoir.getTitle(), id);
        
        // 연관된 댓글들도 함께 삭제 (cascade)
        warMemoirReplyRepository.deleteByWarMemoir(warMemoir);
        
        // 회고록 삭제 (섹션들은 orphanRemoval = true로 자동 삭제)
        warMemoirRepository.delete(warMemoir);
    }

    public boolean existsById(Long id) {
        return warMemoirRepository.existsById(id);
    }
}