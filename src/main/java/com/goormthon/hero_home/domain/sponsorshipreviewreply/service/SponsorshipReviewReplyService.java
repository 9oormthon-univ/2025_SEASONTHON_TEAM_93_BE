package com.goormthon.hero_home.domain.sponsorshipreviewreply.service;

import com.goormthon.hero_home.domain.sponsorshipreview.dto.SponsorshipReviewRequestDto;
import com.goormthon.hero_home.domain.sponsorshipreview.entity.SponsorshipReview;
import com.goormthon.hero_home.domain.sponsorshipreview.repository.SponsorshipReviewRepository;
import com.goormthon.hero_home.domain.sponsorshipreviewreply.converter.SponsorshipReviewReplyConverter;
import com.goormthon.hero_home.domain.sponsorshipreviewreply.dto.SponsorshipReviewReplyResponseDto;
import com.goormthon.hero_home.domain.sponsorshipreviewreply.entity.SponsorshipReviewReply;
import com.goormthon.hero_home.domain.sponsorshipreviewreply.repository.SponsorshipReviewReplyRepository;
import com.goormthon.hero_home.domain.user.entity.User;
import com.goormthon.hero_home.domain.user.repository.UserRepository;
import com.goormthon.hero_home.global.code.status.ErrorStatus;
import com.goormthon.hero_home.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SponsorshipReviewReplyService {

    private final SponsorshipReviewReplyRepository sponsorshipReviewReplyRepository;
    private final SponsorshipReviewRepository sponsorshipReviewRepository;
    private final UserRepository userRepository;

    @Transactional
    public void registerReply(Authentication authentication,
                              SponsorshipReviewRequestDto.SponsorshipReviewInfo sponsorshipReviewInfo) {
        User user = getUser(authentication);

        SponsorshipReview sponsorshipReview = sponsorshipReviewRepository.findById(sponsorshipReviewInfo.getSponsorshipBoardId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.REVIEW_NOT_FOUND));

        SponsorshipReviewReply reply = SponsorshipReviewReply.builder()
                .title(sponsorshipReviewInfo.getTitle())
                .content(sponsorshipReviewInfo.getContent())
                .sponsorshipReview(sponsorshipReview)
                .user(user)
                .build();

        sponsorshipReviewReplyRepository.save(reply);
    }

    @Transactional
    public void deleteReply(Authentication authentication, Long replyId) {
        User user = getUser(authentication);

        SponsorshipReviewReply reply = sponsorshipReviewReplyRepository.findById(replyId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REPLY_NOT_FOUND));

        if (!reply.getUser().getId().equals(user.getId()) && !user.getRole().equals("ADMIN")) {
            throw new GeneralException(ErrorStatus.REPLY_DELETE_NOT_ALLOWED);
        }
        sponsorshipReviewReplyRepository.delete(reply);
    }

    @Transactional(readOnly = true)
    public List<SponsorshipReviewReplyResponseDto.SponsorshipReviewReplyInfo> getAllReply(Long reviewId) {
        return sponsorshipReviewReplyRepository.findBySponsorshipReviewId(reviewId)
                .stream()
                .map(SponsorshipReviewReplyConverter::toReplyInfo)
                .toList();
    }

    public List<SponsorshipReviewReplyResponseDto.SponsorshipReviewReplyInfo> getMyReply(Authentication authentication) {
        User user = getUser(authentication);
        return sponsorshipReviewReplyRepository.findByUser(user)
                .stream()
                .map(SponsorshipReviewReplyConverter::toReplyInfo)
                .toList();
    }

    private User getUser(Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        return user;
    }
}
