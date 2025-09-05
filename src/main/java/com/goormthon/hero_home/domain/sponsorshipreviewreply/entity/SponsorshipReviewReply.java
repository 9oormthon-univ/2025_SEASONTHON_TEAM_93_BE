package com.goormthon.hero_home.domain.sponsorshipreviewreply.entity;

import com.goormthon.hero_home.domain.common.BaseEntity;
import com.goormthon.hero_home.domain.sponsorshipreview.entity.SponsorshipReview;
import com.goormthon.hero_home.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "sponsorship_review_reply")
public class SponsorshipReviewReply extends BaseEntity {

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
