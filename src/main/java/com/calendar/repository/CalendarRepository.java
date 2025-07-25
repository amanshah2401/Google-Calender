package com.calendar.repository;

import com.calendar.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    List<Calendar> findByOwnerId(Long ownerId);

    @Query("SELECT c FROM Calendar c WHERE c.owner.id = :userId OR " +
            "c.id IN (SELECT cs.calendar.id FROM CalendarShare cs WHERE cs.sharedWith.id = :userId)")
    List<Calendar> findAccessibleCalendars(@Param("userId") Long userId);
}