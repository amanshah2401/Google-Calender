package com.calendar.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
// Missing Entity 4: EventRecurrence (for recurring events)
@Entity
@Table(name = "event_recurrences")
public class EventRecurrence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_type", nullable = false)
    private RecurrenceType recurrenceType;

    @Column(name = "recurrence_interval")
    private Integer recurrenceInterval; // e.g., every 2 weeks

    @Column(name = "days_of_week")
    private String daysOfWeek; // JSON string: ["MONDAY", "WEDNESDAY", "FRIDAY"]

    @Column(name = "day_of_month")
    private Integer dayOfMonth; // for monthly recurrence

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "occurrence_count")
    private Integer occurrenceCount; // alternative to end_date

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum RecurrenceType {
        daily, weekly, monthly, yearly
    }

    // Constructors
    public EventRecurrence() {
        this.createdAt = LocalDateTime.now();
        this.recurrenceInterval = 1;
    }

    public EventRecurrence(Event event, RecurrenceType recurrenceType, Integer recurrenceInterval) {
        this();
        this.event = event;
        this.recurrenceType = recurrenceType;
        this.recurrenceInterval = recurrenceInterval;
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

    public RecurrenceType getRecurrenceType() {
        return recurrenceType;
    }

    public void setRecurrenceType(RecurrenceType recurrenceType) {
        this.recurrenceType = recurrenceType;
    }

    public Integer getRecurrenceInterval() {
        return recurrenceInterval;
    }

    public void setRecurrenceInterval(Integer recurrenceInterval) {
        this.recurrenceInterval = recurrenceInterval;
    }

    public String getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public Integer getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(Integer dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
