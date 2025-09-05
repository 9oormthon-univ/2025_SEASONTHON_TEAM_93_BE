package com.goormthon.hero_home.domain.sponsorshipstatus.repository;

import com.goormthon.hero_home.domain.sponsorshipstatus.entity.SponsorshipStatus;
import com.goormthon.hero_home.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SponsorshipStatusRepository extends JpaRepository<SponsorshipStatus, Long> {
    List<SponsorshipStatus> findByUser(User user);
}
