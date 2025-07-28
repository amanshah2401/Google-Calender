package com.calendar.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "calendar_shares")
public class CalendarShare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "calendar_id", nullable = false)
    private Calendar calendar;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "shared_with_id", nullable = false)
    private User sharedWith;

    @Enumerated(EnumType.STRING)
    private Permission permission;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum Permission {
        READ, write
    }

    // Constructors
    public CalendarShare() {
        this.createdAt = LocalDateTime.now();
        this.permission = Permission.READ;
    }

    public CalendarShare(Calendar calendar, User sharedWith, Permission permission) {
        this();
        this.calendar = calendar;
        this.sharedWith = sharedWith;
        this.permission = permission;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Calendar getCalendar() { return calendar; }
    public void setCalendar(Calendar calendar) { this.calendar = calendar; }

    public User getSharedWith() { return sharedWith; }
    public void setSharedWith(User sharedWith) { this.sharedWith = sharedWith; }

    public Permission getPermission() { return permission; }
    public void setPermission(Permission permission) { this.permission = permission; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}