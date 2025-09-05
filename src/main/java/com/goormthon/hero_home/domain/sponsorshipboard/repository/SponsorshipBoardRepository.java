package com.goormthon.hero_home.domain.sponsorshipboard.repository;

import com.goormthon.hero_home.domain.sponsorshipboard.entity.SponsorshipBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SponsorshipBoardRepository extends JpaRepository<SponsorshipBoard, Long> {

    @Modifying
    @Query("UPDATE SponsorshipBoard s SET s.currentAmount = s.currentAmount + :amount WHERE s.id = :boardId")
    int increaseCurrentAmount(@Param("boardId") Long boardId, @Param("amount") Integer amount);
}
