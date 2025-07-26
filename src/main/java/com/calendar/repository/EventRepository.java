package com.calendar.repository;

import com.calendar.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    // Existing methods
    List<Event> findByCalendarId(Long calendarId);
    List<Event> findByCalendarIdIn(List<Long> calendarIds);

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

    // NEW: Additional missing methods
    List<Event> findByCreatedById(Long createdById);

    // Find events by date (for today's events functionality)
    @Query("SELECT e FROM Event e WHERE e.calendar.id IN :calendarIds " +
            "AND DATE(e.startTime) = :date ORDER BY e.startTime")
    List<Event> findEventsByDate(@Param("calendarIds") List<Long> calendarIds,
                                 @Param("date") LocalDate date);

    // Find upcoming events
    @Query("SELECT e FROM Event e WHERE e.calendar.id IN :calendarIds " +
            "AND e.startTime > :currentTime ORDER BY e.startTime LIMIT :limit")
    List<Event> findUpcomingEvents(@Param("calendarIds") List<Long> calendarIds,
                                   @Param("currentTime") LocalDateTime currentTime,
                                   @Param("limit") int limit);

    // Find events with reminders
    @Query("SELECT DISTINCT e FROM Event e JOIN e.reminders r WHERE r.user.id = :userId")
    List<Event> findEventsWithRemindersByUser(@Param("userId") Long userId);

    // Find recurring events
    @Query("SELECT e FROM Event e WHERE e.recurrence IS NOT NULL")
    List<Event> findRecurringEvents();

    // Find events with attachments
    @Query("SELECT DISTINCT e FROM Event e WHERE SIZE(e.attachments) > 0")
    List<Event> findEventsWithAttachments();

    // Find events by location
    @Query("SELECT e FROM Event e WHERE e.calendar.id IN :calendarIds " +
            "AND LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Event> findEventsByLocation(@Param("calendarIds") List<Long> calendarIds,
                                     @Param("location") String location);

    // Count events in date range
    @Query("SELECT COUNT(e) FROM Event e WHERE e.calendar.id IN :calendarIds " +
            "AND e.startTime BETWEEN :start AND :end")
    long countEventsByCalendarsAndDateRange(@Param("calendarIds") List<Long> calendarIds,
                                            @Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end);

    // Find conflicting events (overlapping time slots)
    @Query("SELECT e FROM Event e WHERE e.calendar.id IN :calendarIds " +
            "AND e.id != :excludeEventId " +
            "AND ((e.startTime < :endTime AND e.endTime > :startTime))")
    List<Event> findConflictingEvents(@Param("calendarIds") List<Long> calendarIds,
                                      @Param("excludeEventId") Long excludeEventId,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);
}
