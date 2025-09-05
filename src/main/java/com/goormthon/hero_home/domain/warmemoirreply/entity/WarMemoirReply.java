package com.goormthon.hero_home.domain.warmemoirreply.entity;

import com.goormthon.hero_home.domain.common.BaseEntity;
import com.goormthon.hero_home.domain.user.entity.User;
import com.goormthon.hero_home.domain.warmemoir.entity.WarMemoir;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "war_memoir_reply")
public class WarMemoirReply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "war_memoir_id")
    private WarMemoir warMemoir;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public WarMemoirReply(String title, String content, WarMemoir warMemoir, User user) {
        this.title = title;
        this.content = content;
        this.warMemoir = warMemoir;
        this.user = user;
    }

    public void updateReply(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public boolean isAuthor(User user) {
        return this.user.equals(user);
    }
}
