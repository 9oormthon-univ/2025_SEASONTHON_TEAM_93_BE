package com.goormthon.hero_home.domain.sponsorshipboard.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.goormthon.hero_home.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "sponsorship_board")
public class SponsorshipBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String subTitle;

    private String content;

    private Integer targetAmount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Builder.Default
    @Column(nullable = false)
    private Integer currentAmount = 0;

    @Enumerated(EnumType.STRING)
    private SponsorshipBoardStatus sponsorshipStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
