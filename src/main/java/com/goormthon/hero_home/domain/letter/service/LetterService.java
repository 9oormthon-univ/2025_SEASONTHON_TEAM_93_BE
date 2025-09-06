package com.goormthon.hero_home.domain.letter.service;

import com.goormthon.hero_home.domain.letter.dto.LetterRequestDto;
import com.goormthon.hero_home.domain.letter.dto.LetterResponseDto;
import com.goormthon.hero_home.domain.letter.dto.LetterSummaryDto;
import com.goormthon.hero_home.domain.letter.dto.LetterCountDto;
import com.goormthon.hero_home.domain.letter.entity.Letter;
import com.goormthon.hero_home.domain.letter.repository.LetterRepository;
import com.goormthon.hero_home.domain.user.entity.Role;
import com.goormthon.hero_home.domain.user.entity.User;
import com.goormthon.hero_home.domain.user.repository.UserRepository;
import com.goormthon.hero_home.domain.warmemoir.entity.WarMemoir;
import com.goormthon.hero_home.domain.warmemoir.repository.WarMemoirRepository;
import com.goormthon.hero_home.global.code.status.ErrorStatus;
import com.goormthon.hero_home.global.exception.GeneralException;
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
public class LetterService {

    private final LetterRepository letterRepository;
    private final UserRepository userRepository;
    private final WarMemoirRepository warMemoirRepository;

    public Page<LetterSummaryDto> getAllLetters(String keyword, Boolean isCompleted, Pageable pageable, String userEmail) {
        User currentUser = findUserByEmail(userEmail);
        
        Page<Letter> letters;
        if (currentUser.getRole() == Role.ADMIN) {
            letters = letterRepository.findAllWithFilters(keyword, isCompleted, pageable);
        } else {
            letters = letterRepository.findByUserWithFilters(currentUser, keyword, isCompleted, pageable);
        }
        
        return letters.map(LetterSummaryDto::from);
    }

    public LetterResponseDto getLetterById(Long letterId, String userEmail) {
        Letter letter = findLetterById(letterId);
        User currentUser = findUserByEmail(userEmail);
        
        validateLetterAccess(letter, currentUser);
        
        return LetterResponseDto.from(letter);
    }

    @Transactional
    public LetterResponseDto createLetter(LetterRequestDto requestDto, String userEmail) {
        User user = findUserByEmail(userEmail);
        WarMemoir warMemoir = null;
        
        if (requestDto.getWarMemoirId() != null) {
            warMemoir = warMemoirRepository.findById(requestDto.getWarMemoirId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.WAR_MEMOIR_NOT_FOUND));
        }
        
        Letter letter = Letter.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .user(user)
                .warMemoir(warMemoir)
                .build();
        
        Letter savedLetter = letterRepository.save(letter);
        log.info("편지 생성 완료: letterId={}, userId={}", savedLetter.getId(), user.getId());
        
        return LetterResponseDto.from(letterRepository.findByIdWithFetch(savedLetter.getId()));
    }

    @Transactional
    public LetterResponseDto updateLetter(Long letterId, LetterRequestDto requestDto, String userEmail) {
        Letter letter = findLetterById(letterId);
        User currentUser = findUserByEmail(userEmail);
        
        validateLetterOwnership(letter, currentUser);
        
        WarMemoir warMemoir = null;
        if (requestDto.getWarMemoirId() != null) {
            warMemoir = warMemoirRepository.findById(requestDto.getWarMemoirId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.WAR_MEMOIR_NOT_FOUND));
        }
        
        letter.updateContent(requestDto.getTitle(), requestDto.getContent());
        
        log.info("편지 수정 완료: letterId={}, userId={}", letterId, currentUser.getId());
        
        return LetterResponseDto.from(letter);
    }

    @Transactional
    public void deleteLetter(Long letterId, String userEmail) {
        Letter letter = findLetterById(letterId);
        User currentUser = findUserByEmail(userEmail);
        
        validateLetterOwnership(letter, currentUser);
        
        letterRepository.delete(letter);
        log.info("편지 삭제 완료: letterId={}, userId={}", letterId, currentUser.getId());
    }

    @Transactional
    public LetterResponseDto toggleLetterCompleted(Long letterId, String userEmail) {
        Letter letter = findLetterById(letterId);
        User currentUser = findUserByEmail(userEmail);
        
        validateAdminOnly(currentUser);
        
        letter.toggleCompleted();
        log.info("편지 완료 상태 변경: letterId={}, isCompleted={} (관리자: {})", 
                 letterId, letter.getIsCompleted(), currentUser.getEmail());
        
        return LetterResponseDto.from(letter);
    }

    public LetterCountDto getLetterCountByWarMemoir(Long warMemoirId) {
        WarMemoir warMemoir = warMemoirRepository.findById(warMemoirId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.WAR_MEMOIR_NOT_FOUND));
        
        long totalCount = letterRepository.countByWarMemoir(warMemoir);
        long completedCount = letterRepository.countCompletedByWarMemoir(warMemoir);
        
        log.debug("회고록 ID {}에 대한 편지 개수 조회: 전체 {}, 완료 {}", warMemoirId, totalCount, completedCount);
        
        return LetterCountDto.of(warMemoir.getId(), warMemoir.getTitle(), totalCount, completedCount);
    }

    private Letter findLetterById(Long letterId) {
        Letter letter = letterRepository.findByIdWithFetch(letterId);
        if (letter == null) {
            throw new GeneralException(ErrorStatus.LETTER_NOT_FOUND);
        }
        return letter;
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    private void validateLetterAccess(Letter letter, User user) {
        if (user.getRole() != Role.ADMIN && !letter.isAuthor(user)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }
    }

    private void validateLetterOwnership(Letter letter, User user) {
        if (user.getRole() != Role.ADMIN && !letter.isAuthor(user)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }
    }

    private void validateAdminOnly(User user) {
        if (user.getRole() != Role.ADMIN) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }
    }
}