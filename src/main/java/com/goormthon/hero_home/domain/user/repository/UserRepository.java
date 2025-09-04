package com.goormthon.hero_home.domain.user.repository;

import com.goormthon.hero_home.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
