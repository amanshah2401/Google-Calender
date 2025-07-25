package com.calendar.repository;

import com.calendar.entity.CalendarShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarShareRepository extends JpaRepository<CalendarShare, Long> {
    List<CalendarShare> findByCalendarId(Long calendarId);
    List<CalendarShare> findBySharedWithId(Long userId);
    Optional<CalendarShare> findByCalendarIdAndSharedWithId(Long calendarId, Long userId);
    void deleteByCalendarIdAndSharedWithId(Long calendarId, Long userId);
}