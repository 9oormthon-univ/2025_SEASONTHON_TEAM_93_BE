package com.goormthon.hero_home.domain.sponsorshipreview.entity;

import com.goormthon.hero_home.domain.sponsorshipboard.entity.SponsorshipBoard;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "sponsorship_review")
public class SponsorshipReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sponsorship_board_id")
    private SponsorshipBoard sponsorshipBoard;
}
