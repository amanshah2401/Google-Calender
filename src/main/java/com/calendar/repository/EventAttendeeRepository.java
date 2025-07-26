package com.calendar.repository;

import com.calendar.entity.Event;
import com.calendar.entity.EventAttendee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventAttendeeRepository extends JpaRepository<EventAttendee, Long> {
    List<EventAttendee> findByEventId(Long eventId);
    List<EventAttendee> findByUserId(Long userId);
    List<EventAttendee> findByEmail(String email);
    Optional<EventAttendee> findByEventIdAndUserId(Long eventId, Long userId);
    void deleteByEventIdAndUserId(Long eventId, Long userId);

    @Query("SELECT ea.event FROM EventAttendee ea WHERE ea.user.id = :userId")
    List<Event> findEventsByAttendeeUserId(@Param("userId") Long userId);
}
