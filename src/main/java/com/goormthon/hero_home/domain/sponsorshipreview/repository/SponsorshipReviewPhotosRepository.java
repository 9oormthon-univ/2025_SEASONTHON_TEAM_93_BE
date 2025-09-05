package com.goormthon.hero_home.domain.sponsorshipreview.repository;

import com.goormthon.hero_home.domain.sponsorshipreview.entity.SponsorshipReview;
import com.goormthon.hero_home.domain.sponsorshipreview.entity.SponsorshipReviewPhotos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SponsorshipReviewPhotosRepository extends JpaRepository<SponsorshipReviewPhotos, Long> {
    List<SponsorshipReviewPhotos> findBySponsorshipReview(SponsorshipReview board);
}
