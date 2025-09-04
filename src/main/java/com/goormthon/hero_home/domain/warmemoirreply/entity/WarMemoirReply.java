package com.goormthon.hero_home.domain.warmemoirreply.entity;

import com.goormthon.hero_home.domain.common.BaseEntity;
import com.goormthon.hero_home.domain.user.entity.User;
import com.goormthon.hero_home.domain.warmemoir.entity.WarMemoir;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "war_memoir_reply")
public class WarMemoirReply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "war_memoir_id")
    private WarMemoir warMemoir;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
