package com.calendar.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
// Missing Entity 3: EventReminder
@Entity
@Table(name = "event_reminders")
public class EventReminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "reminder_time", nullable = false)
    private LocalDateTime reminderTime;

    @Column(name = "minutes_before")
    private Integer minutesBefore; // e.g., 15, 30, 60 minutes before event

    @Enumerated(EnumType.STRING)
    private ReminderType type;

    @Column(name = "is_sent")
    private Boolean isSent;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum ReminderType {
        email, push_notification, sms
    }

    // Constructors
    public EventReminder() {
        this.createdAt = LocalDateTime.now();
        this.isSent = false;
        this.type = ReminderType.email;
    }

    public EventReminder(Event event, User user, Integer minutesBefore, ReminderType type) {
        this();
        this.event = event;
        this.user = user;
        this.minutesBefore = minutesBefore;
        this.type = type;
        this.reminderTime = event.getStartTime().minusMinutes(minutesBefore);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(LocalDateTime reminderTime) {
        this.reminderTime = reminderTime;
    }

    public Integer getMinutesBefore() {
        return minutesBefore;
    }

    public void setMinutesBefore(Integer minutesBefore) {
        this.minutesBefore = minutesBefore;
        if (this.event != null) {
            this.reminderTime = this.event.getStartTime().minusMinutes(minutesBefore);
        }
    }

    public ReminderType getType() {
        return type;
    }

    public void setType(ReminderType type) {
        this.type = type;
    }

    public Boolean getIsSent() {
        return isSent;
    }

    public void setIsSent(Boolean isSent) {
        this.isSent = isSent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
