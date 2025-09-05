package com.goormthon.hero_home.domain.sponsorshipstatus.repository;

import com.goormthon.hero_home.domain.sponsorshipstatus.entity.SponsorshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SponsorshipStatusRepository extends JpaRepository<SponsorshipStatus, Long> {
}
