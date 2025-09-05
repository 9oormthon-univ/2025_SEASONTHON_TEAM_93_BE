package com.goormthon.hero_home.domain.warmemoir.repository;

import com.goormthon.hero_home.domain.warmemoir.entity.SubWarMemoir;
import com.goormthon.hero_home.domain.warmemoir.entity.WarMemoir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubWarMemoirRepository extends JpaRepository<SubWarMemoir, Long> {

    @Query("SELECT s FROM SubWarMemoir s WHERE s.warMemoir = :warMemoir ORDER BY s.sectionOrder ASC")
    List<SubWarMemoir> findByWarMemoirOrderBySectionOrderAsc(@Param("warMemoir") WarMemoir warMemoir);

    @Query("SELECT s FROM SubWarMemoir s WHERE s.warMemoir.id = :warMemoirId ORDER BY s.sectionOrder ASC")
    List<SubWarMemoir> findByWarMemoirIdOrderBySectionOrderAsc(@Param("warMemoirId") Long warMemoirId);

    void deleteByWarMemoir(WarMemoir warMemoir);

    @Query("SELECT COALESCE(MAX(s.sectionOrder), 0) FROM SubWarMemoir s WHERE s.warMemoir = :warMemoir")
    Integer findMaxSectionOrderByWarMemoir(@Param("warMemoir") WarMemoir warMemoir);
}