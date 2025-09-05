package com.goormthon.hero_home.domain.warmemoir.entity;

import com.goormthon.hero_home.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "war_memoir")
public class WarMemoir extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String image;

    @OneToMany(mappedBy = "warMemoir", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SubWarMemoir> subWarMemoirs = new ArrayList<>();

    @Builder
    public WarMemoir(String title, String image) {
        this.title = title;
        this.image = image;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateImage(String image) {
        this.image = image;
    }

    public void updateMemoir(String title, String image) {
        this.title = title;
        this.image = image;
    }
}
