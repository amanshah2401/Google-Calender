package com.calendar.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
// Missing Entity 2: EventAttendee
@Entity
@Table(name = "event_attendees")
public class EventAttendee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Can be null for external attendees

    @Column(nullable = false)
    private String email;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "is_organizer")
    private Boolean isOrganizer;

    @Enumerated(EnumType.STRING)
    @Column(name = "response_status")
    private ResponseStatus responseStatus;

    @Column(name = "invited_at")
    private LocalDateTime invitedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    public enum ResponseStatus {
        pending, accepted, declined, tentative
    }

    // Constructors
    public EventAttendee() {
        this.invitedAt = LocalDateTime.now();
        this.responseStatus = ResponseStatus.pending;
        this.isOrganizer = false;
    }

    public EventAttendee(Event event, User user, String email, String displayName, Boolean isOrganizer) {
        this();
        this.event = event;
        this.user = user;
        this.email = email;
        this.displayName = displayName;
        this.isOrganizer = isOrganizer;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getIsOrganizer() {
        return isOrganizer;
    }

    public void setIsOrganizer(Boolean isOrganizer) {
        this.isOrganizer = isOrganizer;
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
        if (responseStatus != ResponseStatus.pending) {
            this.respondedAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getInvitedAt() {
        return invitedAt;
    }

    public void setInvitedAt(LocalDateTime invitedAt) {
        this.invitedAt = invitedAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }
}
