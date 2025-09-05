package com.goormthon.hero_home.domain.sponsorshipboard.repository;

import com.goormthon.hero_home.domain.sponsorshipboard.entity.SponsorshipBoard;
import com.goormthon.hero_home.domain.sponsorshipboard.entity.SponsorshipBoardPhotos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SponsorshipBoardPhotosRepository extends JpaRepository<SponsorshipBoardPhotos, Long> {
    List<SponsorshipBoardPhotos> findBySponsorshipBoard(SponsorshipBoard board);
}
