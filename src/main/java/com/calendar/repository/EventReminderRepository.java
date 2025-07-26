package com.calendar.repository;

import com.calendar.entity.EventReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

// Missing Repository 1: EventReminderRepository
@Repository
public interface EventReminderRepository extends JpaRepository<EventReminder, Long> {
    List<EventReminder> findByEventId(Long eventId);
    List<EventReminder> findByUserId(Long userId);
    List<EventReminder> findByEventIdAndUserId(Long eventId, Long userId);
    void deleteByEventIdAndUserId(Long eventId, Long userId);

    // Find reminders that need to be sent
    @Query("SELECT er FROM EventReminder er WHERE er.reminderTime <= :currentTime AND er.isSent = false")
    List<EventReminder> findPendingReminders(@Param("currentTime") LocalDateTime currentTime);

    // Find user's reminders for a specific time range
    @Query("SELECT er FROM EventReminder er WHERE er.user.id = :userId " +
            "AND er.reminderTime BETWEEN :start AND :end")
    List<EventReminder> findUserRemindersInRange(@Param("userId") Long userId,
                                                 @Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);
}

