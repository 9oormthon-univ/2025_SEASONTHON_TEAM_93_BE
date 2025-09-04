package com.goormthon.hero_home.domain.sponsorshipstatus.entity;

import com.goormthon.hero_home.domain.sponsorshipboard.entity.SponsorshipBoard;
import com.goormthon.hero_home.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "sponsorship_status")
public class SponsorshipStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer sponsorshipAmount;

    private Boolean isApproved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sponsorship_board_id")
    private SponsorshipBoard sponsorshipBoard;
}
