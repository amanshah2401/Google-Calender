package com.calendar.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// Missing Entity 1: EventShare
@Entity
@Table(name = "event_shares")
public class EventShare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "shared_with_id", nullable = false)
    private User sharedWith;

    @Enumerated(EnumType.STRING)
    private Permission permission;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum Permission {
        read, write
    }

    // Constructors
    public EventShare() {
        this.createdAt = LocalDateTime.now();
        this.permission = Permission.read;
    }

    public EventShare(Event event, User sharedWith, Permission permission) {
        this();
        this.event = event;
        this.sharedWith = sharedWith;
        this.permission = permission;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public User getSharedWith() { return sharedWith; }
    public void setSharedWith(User sharedWith) { this.sharedWith = sharedWith; }

    public Permission getPermission() { return permission; }
    public void setPermission(Permission permission) { this.permission = permission; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

