package com.goormthon.hero_home.domain.letter.entity;

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
@Table(name = "letter")
public class Letter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Boolean isCompleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "war_memoir_id")
    private WarMemoir warMemoir;

    @Builder
    public Letter(String title, String content, User user, WarMemoir warMemoir) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.warMemoir = warMemoir;
        this.isCompleted = false;
    }

    public void updateContent(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void toggleCompleted() {
        this.isCompleted = !this.isCompleted;
    }

    public boolean isAuthor(User user) {
        return this.user.equals(user);
    }
}
