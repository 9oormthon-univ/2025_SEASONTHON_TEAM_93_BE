package com.goormthon.hero_home.domain.warmemoirreply.repository;

import com.goormthon.hero_home.domain.user.entity.User;
import com.goormthon.hero_home.domain.warmemoir.entity.WarMemoir;
import com.goormthon.hero_home.domain.warmemoirreply.entity.WarMemoirReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WarMemoirReplyRepository extends JpaRepository<WarMemoirReply, Long> {

    @Query("SELECT r FROM WarMemoirReply r WHERE r.warMemoir = :warMemoir")
    Page<WarMemoirReply> findByWarMemoir(@Param("warMemoir") WarMemoir warMemoir, Pageable pageable);

    @Query("SELECT r FROM WarMemoirReply r WHERE r.warMemoir.id = :warMemoirId")
    Page<WarMemoirReply> findByWarMemoirId(@Param("warMemoirId") Long warMemoirId, Pageable pageable);

    @Query("SELECT r FROM WarMemoirReply r JOIN FETCH r.user WHERE r.warMemoir.id = :warMemoirId ORDER BY r.createdAt DESC")
    List<WarMemoirReply> findByWarMemoirIdWithUser(@Param("warMemoirId") Long warMemoirId);

    @Query("SELECT COUNT(r) FROM WarMemoirReply r WHERE r.warMemoir = :warMemoir")
    Long countByWarMemoir(@Param("warMemoir") WarMemoir warMemoir);

    @Query("SELECT COUNT(r) FROM WarMemoirReply r WHERE r.warMemoir.id = :warMemoirId")
    Long countByWarMemoirId(@Param("warMemoirId") Long warMemoirId);

    void deleteByWarMemoir(WarMemoir warMemoir);

    void deleteByUser(User user);
}