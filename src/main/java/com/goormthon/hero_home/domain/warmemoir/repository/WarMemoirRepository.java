package com.goormthon.hero_home.domain.warmemoir.repository;

import com.goormthon.hero_home.domain.warmemoir.entity.WarMemoir;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WarMemoirRepository extends JpaRepository<WarMemoir, Long> {

    @Query("SELECT w FROM WarMemoir w")
    Page<WarMemoir> findAll(Pageable pageable);

    @Query("SELECT w FROM WarMemoir w WHERE w.title LIKE %:keyword%")
    Page<WarMemoir> findByTitleContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT w FROM WarMemoir w LEFT JOIN FETCH w.subWarMemoirs s ORDER BY w.createdAt DESC, s.sectionOrder ASC")
    List<WarMemoir> findAllWithSubMemoirs();

    @Query("SELECT w FROM WarMemoir w LEFT JOIN FETCH w.subWarMemoirs s WHERE w.id = :id ORDER BY s.sectionOrder ASC")
    Optional<WarMemoir> findByIdWithSubMemoirs(@Param("id") Long id);
}