package com.calendar.dto;

import com.calendar.entity.EventRecurrence;

import java.time.LocalDate;

public class UpdateRecurrenceRequest {
    private EventRecurrence.RecurrenceType recurrenceType;
    private Integer interval;
    private LocalDate endDate;
    private Integer occurrenceCount;

    // Getters and setters
    public EventRecurrence.RecurrenceType getRecurrenceType() {
        return recurrenceType;
    }

    public void setRecurrenceType(EventRecurrence.RecurrenceType recurrenceType) {
        this.recurrenceType = recurrenceType;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getOccurrenceCount() {
        return occurrenceCount;
    }

    public void setOccurrenceCount(Integer occurrenceCount) {
        this.occurrenceCount = occurrenceCount;
    }
}
