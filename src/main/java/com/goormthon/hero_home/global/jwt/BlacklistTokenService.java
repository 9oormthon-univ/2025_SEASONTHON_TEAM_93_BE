package com.goormthon.hero_home.global.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class BlacklistTokenService {

    private final ConcurrentHashMap<String, Date> blacklistedTokens = new ConcurrentHashMap<>();
    private final JwtTokenProvider jwtTokenProvider;

    public BlacklistTokenService(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void blacklistToken(String token) {
        try {
            // 토큰에서 만료 시간 추출
            Date expirationDate = getExpirationFromToken(token);
            if (expirationDate != null) {
                blacklistedTokens.put(token, expirationDate);
                log.info("토큰이 블랙리스트에 추가됨. 만료 예정: {}", expirationDate);
                
                // 만료된 토큰들 정리
                cleanupExpiredTokens();
            }
        } catch (Exception e) {
            log.error("토큰 블랙리스트 추가 중 오류 발생", e);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        // 만료된 토큰들 먼저 정리
        cleanupExpiredTokens();
        
        boolean isBlacklisted = blacklistedTokens.containsKey(token);
        if (isBlacklisted) {
            log.debug("블랙리스트된 토큰 감지: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
        }
        return isBlacklisted;
    }

    private Date getExpirationFromToken(String token) {
        try {
            return jwtTokenProvider.getExpirationFromToken(token);
        } catch (Exception e) {
            log.warn("토큰에서 만료 시간 추출 실패: {}", e.getMessage());
            // 만료 시간을 알 수 없는 경우, 24시간 후로 설정
            return new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
        }
    }

    private void cleanupExpiredTokens() {
        Date now = new Date();
        blacklistedTokens.entrySet().removeIf(entry -> {
            boolean isExpired = entry.getValue().before(now);
            if (isExpired) {
                log.debug("만료된 블랙리스트 토큰 제거: {}", 
                    entry.getKey().substring(0, Math.min(entry.getKey().length(), 20)) + "...");
            }
            return isExpired;
        });
    }

    public int getBlacklistedTokenCount() {
        cleanupExpiredTokens();
        return blacklistedTokens.size();
    }
}