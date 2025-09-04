package com.goormthon.hero_home.domain.sponsorshipreviewreply.entity;

import com.goormthon.hero_home.domain.sponsorshipreview.entity.SponsorshipReview;
import com.goormthon.hero_home.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "sponsorship_review_reply")
public class SponsorshipReviewReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sponsorship_review_id")
    private SponsorshipReview sponsorshipReview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
