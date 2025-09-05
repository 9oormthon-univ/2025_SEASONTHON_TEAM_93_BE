package com.goormthon.hero_home.domain.user.service;

import com.goormthon.hero_home.domain.user.entity.Role;
import com.goormthon.hero_home.domain.user.entity.SocialType;
import com.goormthon.hero_home.domain.user.entity.User;
import com.goormthon.hero_home.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public User getUserBySocialId(String socialId) {
        return userRepository.findBySocialId(socialId).orElse(null);
    }

    @Transactional
    public User createOauth(String socialId, String email, SocialType socialType) {
        User newUser = User.builder()
                .socialId(socialId)
                .email(email)
                .socialType(socialType)
                .role(Role.USER)
                .build();
        
        User savedUser = userRepository.save(newUser);
        log.info("새로운 소셜 사용자 생성: {}", savedUser.getEmail());
        return savedUser;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        
        log.info("회원 탈퇴 처리: {} (ID: {})", user.getEmail(), userId);
        
        // TODO: 관련 데이터 처리 (후원)
        // - 사용자의 후원 내역 처리
        // - 사용자의 댓글 처리
        
        // 하드 삭제 (실제 데이터 삭제)
        userRepository.delete(user);
        
        log.info("회원 탈퇴 완료: {}", user.getEmail());
    }
}