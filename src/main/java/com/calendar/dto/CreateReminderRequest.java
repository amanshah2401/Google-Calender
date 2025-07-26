package com.calendar.dto;

import com.calendar.entity.EventReminder;

// DTO for CreateReminderRequest
public class CreateReminderRequest {
    private Integer minutesBefore;
    private EventReminder.ReminderType type;

    public Integer getMinutesBefore() {
        return minutesBefore;
    }

    public void setMinutesBefore(Integer minutesBefore) {
        this.minutesBefore = minutesBefore;
    }

    public EventReminder.ReminderType getType() {
        return type;
    }

    public void setType(EventReminder.ReminderType type) {
        this.type = type;
    }
}
