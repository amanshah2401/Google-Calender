package com.calendar.repository;

import com.calendar.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCalendarId(Long calendarId);

    List<Event> findByCalendarIdAndStartTimeBetween(Long calendarId,
                                                    LocalDateTime start,
                                                    LocalDateTime end);

    @Query("SELECT e FROM Event e WHERE e.calendar.id IN :calendarIds " +
            "AND e.startTime BETWEEN :start AND :end ORDER BY e.startTime")
    List<Event> findEventsByCalendarsAndDateRange(@Param("calendarIds") List<Long> calendarIds,
                                                  @Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end);

    @Query("SELECT e FROM Event e WHERE e.calendar.id IN :calendarIds " +
            "AND (LOWER(e.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Event> searchEvents(@Param("calendarIds") List<Long> calendarIds,
                             @Param("query") String query);
}