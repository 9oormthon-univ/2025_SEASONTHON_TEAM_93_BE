package com.goormthon.hero_home.domain.warmemoir.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
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

    private Integer sectionOrder;

    private String title; //소제목

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "war_memoir_id")
    private WarMemoir warMemoir;
}
