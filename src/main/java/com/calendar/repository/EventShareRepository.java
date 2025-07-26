package com.calendar.repository;

import com.calendar.entity.Event;
import com.calendar.entity.EventShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventShareRepository extends JpaRepository<EventShare, Long> {
    List<EventShare> findByEventId(Long eventId);
    List<EventShare> findBySharedWithId(Long userId);
    Optional<EventShare> findByEventIdAndSharedWithId(Long eventId, Long userId);
    void deleteByEventIdAndSharedWithId(Long eventId, Long userId);

    @Query("SELECT es.event FROM EventShare es WHERE es.sharedWith.id = :userId")
    List<Event> findSharedEventsByUserId(@Param("userId") Long userId);
}
