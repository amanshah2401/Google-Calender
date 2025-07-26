package com.calendar.repository;

import com.calendar.entity.EventComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // Correct import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventCommentRepository extends JpaRepository<EventComment, Long> {
    List<EventComment> findByEventId(Long eventId);

    List<EventComment> findByUserId(Long userId);

    List<EventComment> findByEventIdOrderByCreatedAtAsc(Long eventId);

    List<EventComment> findByEventIdOrderByCreatedAtDesc(Long eventId);

    // Count comments for an event
    long countByEventId(Long eventId);

    // Find recent comments by user
    @Query("SELECT ec FROM EventComment ec WHERE ec.user.id = :userId ORDER BY ec.createdAt DESC")
    Page<EventComment> findRecentCommentsByUser(@Param("userId") Long userId, Pageable pageable); // Correct Pageable
}