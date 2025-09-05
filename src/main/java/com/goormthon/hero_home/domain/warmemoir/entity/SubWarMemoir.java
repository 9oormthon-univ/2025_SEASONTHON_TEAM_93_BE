package com.goormthon.hero_home.domain.warmemoir.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "sub_war_memoir")
public class SubWarMemoir {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer sectionOrder;

    @Column(nullable = false)
    private String title; //소제목

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "war_memoir_id")
    private WarMemoir warMemoir;

    @Builder
    public SubWarMemoir(Integer sectionOrder, String title, String content, WarMemoir warMemoir) {
        this.sectionOrder = sectionOrder;
        this.title = title;
        this.content = content;
        this.warMemoir = warMemoir;
    }

    public void updateSection(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void updateSectionOrder(Integer sectionOrder) {
        this.sectionOrder = sectionOrder;
    }
}
