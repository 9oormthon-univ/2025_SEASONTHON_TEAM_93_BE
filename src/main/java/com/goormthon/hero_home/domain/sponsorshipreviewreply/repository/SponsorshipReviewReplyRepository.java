package com.goormthon.hero_home.domain.sponsorshipreviewreply.repository;

import com.goormthon.hero_home.domain.sponsorshipreviewreply.entity.SponsorshipReviewReply;
import com.goormthon.hero_home.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SponsorshipReviewReplyRepository extends JpaRepository<SponsorshipReviewReply, Long> {
    List<SponsorshipReviewReply> findBySponsorshipReviewId(Long reviewId);
    List<SponsorshipReviewReply> findByUser(User user);
}
