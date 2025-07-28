package com.calendar.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "created_at",columnDefinition = "DATETIME(6)")
    private LocalDateTime createdAt;

    @JsonManagedReference
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Calendar> calendars = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Event> createdEvents = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "sharedWith", cascade = CascadeType.ALL)
    private List<CalendarShare> sharedCalendars = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "sharedWith", cascade = CascadeType.ALL)
    private List<EventShare> sharedEvents = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<EventAttendee> eventAttendances = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<EventReminder> eventReminders = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<EventComment> eventComments = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "uploadedBy", cascade = CascadeType.ALL)
    private List<EventAttachment> uploadedAttachments = new ArrayList<>();

    public User() {

    }

    public User(String email, String password, String firstName, String lastName) {
        this.createdAt = LocalDateTime.now();
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters and Setters (existing ones)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Calendar> getCalendars() {
        return calendars;
    }

    public void setCalendars(List<Calendar> calendars) {
        this.calendars = calendars;
    }

    // NEW: Getters and setters for missing relationships
    public List<Event> getCreatedEvents() {
        return createdEvents;
    }

    public void setCreatedEvents(List<Event> createdEvents) {
        this.createdEvents = createdEvents;
    }

    public List<CalendarShare> getSharedCalendars() {
        return sharedCalendars;
    }

    public void setSharedCalendars(List<CalendarShare> sharedCalendars) {
        this.sharedCalendars = sharedCalendars;
    }

    public List<EventShare> getSharedEvents() {
        return sharedEvents;
    }

    public void setSharedEvents(List<EventShare> sharedEvents) {
        this.sharedEvents = sharedEvents;
    }

    public List<EventAttendee> getEventAttendances() {
        return eventAttendances;
    }

    public void setEventAttendances(List<EventAttendee> eventAttendances) {
        this.eventAttendances = eventAttendances;
    }

    public List<EventReminder> getEventReminders() {
        return eventReminders;
    }

    public void setEventReminders(List<EventReminder> eventReminders) {
        this.eventReminders = eventReminders;
    }

    public List<EventComment> getEventComments() {
        return eventComments;
    }

    public void setEventComments(List<EventComment> eventComments) {
        this.eventComments = eventComments;
    }

    public List<EventAttachment> getUploadedAttachments() {
        return uploadedAttachments;
    }

    public void setUploadedAttachments(List<EventAttachment> uploadedAttachments) {
        this.uploadedAttachments = uploadedAttachments;
    }

    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void addCalendar(Calendar calendar) {
        calendars.add(calendar);
        calendar.setOwner(this);
    }

    public void removeCalendar(Calendar calendar) {
        calendars.remove(calendar);
        calendar.setOwner(null);
    }
}
