package com.goormthon.hero_home.domain.user.entity;

import com.goormthon.hero_home.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    private String email;

    private String phone_num;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String socialId;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Builder
    public User(String name, String address, String email, String phone_num, Role role, String socialId, SocialType socialType) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone_num = phone_num;
        this.role = role;
        this.socialId = socialId;
        this.socialType = socialType;
    }
}
