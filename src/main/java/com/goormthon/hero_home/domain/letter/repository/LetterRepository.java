package com.goormthon.hero_home.domain.letter.repository;

import com.goormthon.hero_home.domain.letter.entity.Letter;
import com.goormthon.hero_home.domain.user.entity.User;
import com.goormthon.hero_home.domain.warmemoir.entity.WarMemoir;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LetterRepository extends JpaRepository<Letter, Long> {

    @Query("SELECT l FROM Letter l JOIN FETCH l.user u LEFT JOIN FETCH l.warMemoir w " +
           "WHERE (:keyword IS NULL OR l.title LIKE %:keyword% OR l.content LIKE %:keyword%) " +
           "AND (:isCompleted IS NULL OR l.isCompleted = :isCompleted)")
    Page<Letter> findAllWithFilters(@Param("keyword") String keyword, 
                                  @Param("isCompleted") Boolean isCompleted, 
                                  Pageable pageable);

    @Query("SELECT l FROM Letter l JOIN FETCH l.user u LEFT JOIN FETCH l.warMemoir w " +
           "WHERE l.user = :user " +
           "AND (:keyword IS NULL OR l.title LIKE %:keyword% OR l.content LIKE %:keyword%) " +
           "AND (:isCompleted IS NULL OR l.isCompleted = :isCompleted)")
    Page<Letter> findByUserWithFilters(@Param("user") User user,
                                     @Param("keyword") String keyword, 
                                     @Param("isCompleted") Boolean isCompleted, 
                                     Pageable pageable);

    @Query("SELECT l FROM Letter l JOIN FETCH l.user u LEFT JOIN FETCH l.warMemoir w " +
           "WHERE l.warMemoir = :warMemoir")
    Page<Letter> findByWarMemoir(@Param("warMemoir") WarMemoir warMemoir, Pageable pageable);

    @Query("SELECT l FROM Letter l JOIN FETCH l.user u LEFT JOIN FETCH l.warMemoir w " +
           "WHERE l.id = :id")
    Letter findByIdWithFetch(@Param("id") Long id);

    @Query("SELECT COUNT(l) FROM Letter l WHERE l.user = :user")
    long countByUser(@Param("user") User user);

    @Query("SELECT COUNT(l) FROM Letter l WHERE l.user = :user AND l.isCompleted = true")
    long countByUserAndCompleted(@Param("user") User user);

    @Query("SELECT COUNT(l) FROM Letter l WHERE l.warMemoir = :warMemoir")
    long countByWarMemoir(@Param("warMemoir") WarMemoir warMemoir);

    @Query("SELECT COUNT(l) FROM Letter l WHERE l.warMemoir = :warMemoir AND l.isCompleted = true")
    long countCompletedByWarMemoir(@Param("warMemoir") WarMemoir warMemoir);

    boolean existsByIdAndUser(Long id, User user);
}