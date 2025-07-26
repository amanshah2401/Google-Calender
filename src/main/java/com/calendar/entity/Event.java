package com.calendar.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Updated Event Entity with missing relationships
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    private String location;

    @ManyToOne
    @JoinColumn(name = "calendar_id", nullable = false)
    private Calendar calendar;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // NEW: Missing relationships
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventShare> eventShares = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventAttendee> attendees = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventReminder> reminders = new ArrayList<>();

    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private EventRecurrence recurrence;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventAttachment> attachments = new ArrayList<>();

    // Constructors
    public Event() {
        this.createdAt = LocalDateTime.now();
    }

    public Event(String title, String description, LocalDateTime startTime,
                 LocalDateTime endTime, Calendar calendar, User createdBy) {
        this();
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.calendar = calendar;
        this.createdBy = createdBy;
    }

    // Getters and Setters (existing ones + new ones)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Calendar getCalendar() { return calendar; }
    public void setCalendar(Calendar calendar) { this.calendar = calendar; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // NEW: Getters and setters for missing relationships
    public List<EventShare> getEventShares() { return eventShares; }
    public void setEventShares(List<EventShare> eventShares) { this.eventShares = eventShares; }

    public List<EventAttendee> getAttendees() { return attendees; }
    public void setAttendees(List<EventAttendee> attendees) { this.attendees = attendees; }

    public List<EventReminder> getReminders() { return reminders; }
    public void setReminders(List<EventReminder> reminders) { this.reminders = reminders; }

    public EventRecurrence getRecurrence() { return recurrence; }
    public void setRecurrence(EventRecurrence recurrence) { this.recurrence = recurrence; }

    public List<EventComment> getComments() { return comments; }
    public void setComments(List<EventComment> comments) { this.comments = comments; }

    public List<EventAttachment> getAttachments() { return attachments; }
    public void setAttachments(List<EventAttachment> attachments) { this.attachments = attachments; }

    // Helper methods
    public void addAttendee(EventAttendee attendee) {
        attendees.add(attendee);
        attendee.setEvent(this);
    }

    public void removeAttendee(EventAttendee attendee) {
        attendees.remove(attendee);
        attendee.setEvent(null);
    }

    public void addComment(EventComment comment) {
        comments.add(comment);
        comment.setEvent(this);
    }

    public void removeComment(EventComment comment) {
        comments.remove(comment);
        comment.setEvent(null);
    }

    public void addAttachment(EventAttachment attachment) {
        attachments.add(attachment);
        attachment.setEvent(this);
    }

    public void removeAttachment(EventAttachment attachment) {
        attachments.remove(attachment);
        attachment.setEvent(null);
    }
}

