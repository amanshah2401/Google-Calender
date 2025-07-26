package com.calendar.repository;

import com.calendar.entity.EventRecurrence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Missing Repository 2: EventRecurrenceRepository
@Repository
public interface EventRecurrenceRepository extends JpaRepository<EventRecurrence, Long> {
    Optional<EventRecurrence> findByEventId(Long eventId);

    List<EventRecurrence> findByRecurrenceType(EventRecurrence.RecurrenceType recurrenceType);

    void deleteByEventId(Long eventId);

    // Find all recurring events
    @Query("SELECT er FROM EventRecurrence er")
    List<EventRecurrence> findAllRecurringEvents();

    // Find recurring events that need to generate new instances
    @Query("SELECT er FROM EventRecurrence er WHERE " +
            "(er.endDate IS NULL OR er.endDate >= :currentDate) AND " +
            "(er.occurrenceCount IS NULL OR er.occurrenceCount > 0)")
    List<EventRecurrence> findActiveRecurrences(@Param("currentDate") LocalDate currentDate);
}
