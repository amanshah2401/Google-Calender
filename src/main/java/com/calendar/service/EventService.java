package com.calendar.service;

import com.calendar.entity.*;
import com.calendar.entity.Calendar;
import com.calendar.exception.AccessDeniedException;
import com.calendar.exception.EventNotFoundException;
import com.calendar.exception.InvalidEventTimeException;
import com.calendar.repository.EventAttendeeRepository;
import com.calendar.repository.EventRepository;
import com.calendar.repository.EventShareRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventShareRepository eventShareRepository;

    @Autowired
    private EventAttendeeRepository eventAttendeeRepository;

    public Event createEvent(String title, String description, LocalDateTime startTime,
                             LocalDateTime endTime, String location, Long calendarId, Long userId) {
        if (startTime.isAfter(endTime)) {
            throw new InvalidEventTimeException("Start time must be before end time");
        }

        Calendar calendar = calendarService.getCalendarById(calendarId, userId);
        User user = userService.findById(userId);

        Event event = new Event(title, description, startTime, endTime, calendar, user);
        event.setLocation(location);

        Event savedEvent = eventRepository.save(event);

        // Add creator as organizer
        EventAttendee organizer = new EventAttendee(savedEvent, user, user.getEmail(),
                user.getFirstName() + " " + user.getLastName(), true);
        organizer.setResponseStatus(EventAttendee.ResponseStatus.accepted);
        eventAttendeeRepository.save(organizer);

        return savedEvent;
    }

    public List<Event> getCalendarEvents(Long calendarId, Long userId) {
        // Verify access to calendar
        calendarService.getCalendarById(calendarId, userId);
        return eventRepository.findByCalendarId(calendarId);
    }

    public List<Event> getUserEvents(Long userId) {
        // Get events from user's calendars
        List<Calendar> userCalendars = calendarService.getUserCalendars(userId);
        List<Long> calendarIds = userCalendars.stream()
                .map(Calendar::getId)
                .collect(Collectors.toList());

        List<Event> calendarEvents = eventRepository.findByCalendarIdIn(calendarIds);

        // Get shared events
        List<Event> sharedEvents = eventShareRepository.findSharedEventsByUserId(userId);

        // Get events where user is attendee
        List<Event> attendeeEvents = eventAttendeeRepository.findEventsByAttendeeUserId(userId);

        // Combine and remove duplicates
        Set<Event> allEvents = new HashSet<>();
        allEvents.addAll(calendarEvents);
        allEvents.addAll(sharedEvents);
        allEvents.addAll(attendeeEvents);

        return new ArrayList<>(allEvents);
    }

    public List<Event> getEventsInDateRange(List<Long> calendarIds, LocalDateTime start,
                                            LocalDateTime end, Long userId) {
        // Verify access to all calendars
        for (Long calendarId : calendarIds) {
            calendarService.getCalendarById(calendarId, userId);
        }

        return eventRepository.findEventsByCalendarsAndDateRange(calendarIds, start, end);
    }

    public Event getEventById(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found: " + eventId));

        if (!hasAccessToEvent(event, userId)) {
            throw new AccessDeniedException("No access to event: " + eventId);
        }

        return event;
    }

    public Event updateEvent(Long eventId, String title, String description,
                             LocalDateTime startTime, LocalDateTime endTime,
                             String location, Long userId) {
        Event event = getEventById(eventId, userId);

        if (!hasWritePermissionToEvent(event, userId)) {
            throw new AccessDeniedException("No write permission for event: " + eventId);
        }

        if (startTime.isAfter(endTime)) {
            throw new InvalidEventTimeException("Start time must be before end time");
        }

        event.setTitle(title);
        event.setDescription(description);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setLocation(location);

        return eventRepository.save(event);
    }

    public void deleteEvent(Long eventId, Long userId) {
        Event event = getEventById(eventId, userId);

        if (!hasWritePermissionToEvent(event, userId)) {
            throw new AccessDeniedException("No write permission to delete event: " + eventId);
        }

        eventRepository.delete(event);
    }

    public EventShare shareEvent(Long eventId, String userEmail, EventShare.Permission permission, Long ownerId) {
        Event event = getEventById(eventId, ownerId);

        if (!hasWritePermissionToEvent(event, ownerId)) {
            throw new AccessDeniedException("Only organizer can share event: " + eventId);
        }

        User userToShareWith = userService.findByEmail(userEmail);

        // Check if already shared
        Optional<EventShare> existingShare = eventShareRepository
                .findByEventIdAndSharedWithId(eventId, userToShareWith.getId());

        if (existingShare.isPresent()) {
            EventShare share = existingShare.get();
            share.setPermission(permission);
            return eventShareRepository.save(share);
        }

        EventShare newShare = new EventShare(event, userToShareWith, permission);
        return eventShareRepository.save(newShare);
    }

    public void removeEventShare(Long eventId, Long userIdToRemove, Long ownerId) {
        Event event = getEventById(eventId, ownerId);

        if (!hasWritePermissionToEvent(event, ownerId)) {
            throw new AccessDeniedException("Only organizer can remove shares: " + eventId);
        }

        eventShareRepository.deleteByEventIdAndSharedWithId(eventId, userIdToRemove);
    }

    public EventAttendee inviteUserToEvent(Long eventId, String userEmail, Long organizerId) {
        Event event = getEventById(eventId, organizerId);

        if (!hasWritePermissionToEvent(event, organizerId)) {
            throw new AccessDeniedException("Only organizer can invite users: " + eventId);
        }

        User userToInvite = userService.findByEmail(userEmail);

        // Check if already invited
        Optional<EventAttendee> existingAttendee = eventAttendeeRepository
                .findByEventIdAndUserId(eventId, userToInvite.getId());

        if (existingAttendee.isPresent()) {
            throw new IllegalStateException("User already invited to event");
        }

        EventAttendee attendee = new EventAttendee(event, userToInvite, userToInvite.getEmail(),
                userToInvite.getFirstName() + " " + userToInvite.getLastName(), false);
        return eventAttendeeRepository.save(attendee);
    }

    public EventAttendee respondToEventInvitation(Long eventId, Long userId, EventAttendee.ResponseStatus response) {
        EventAttendee attendee = eventAttendeeRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new EventNotFoundException("Event invitation not found"));

        attendee.setResponseStatus(response);
        return eventAttendeeRepository.save(attendee);
    }

    public List<EventAttendee> getEventAttendees(Long eventId, Long userId) {
        Event event = getEventById(eventId, userId);
        return eventAttendeeRepository.findByEventId(eventId);
    }

    public List<Event> searchEvents(String query, Long userId) {
        List<Event> userEvents = getUserEvents(userId);

        return userEvents.stream()
                .filter(event ->
                        event.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                                (event.getDescription() != null &&
                                        event.getDescription().toLowerCase().contains(query.toLowerCase())) ||
                                (event.getLocation() != null &&
                                        event.getLocation().toLowerCase().contains(query.toLowerCase()))
                )
                .collect(Collectors.toList());
    }

    public List<Event> getTodayEvents(Long userId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);

        return getUserEvents(userId).stream()
                .filter(event ->
                        !event.getStartTime().isAfter(endOfDay) &&
                                !event.getEndTime().isBefore(startOfDay)
                )
                .sorted((e1, e2) -> e1.getStartTime().compareTo(e2.getStartTime()))
                .collect(Collectors.toList());
    }

    private boolean hasAccessToEvent(Event event, Long userId) {
        // Creator has access
        if (event.getCreatedBy().getId().equals(userId)) {
            return true;
        }

        // Check calendar access
        try {
            calendarService.getCalendarById(event.getCalendar().getId(), userId);
            return true;
        } catch (AccessDeniedException e) {
            // No calendar access, check other permissions
        }

        // Check if event is shared with user
        Optional<EventShare> eventShare = eventShareRepository
                .findByEventIdAndSharedWithId(event.getId(), userId);
        if (eventShare.isPresent()) {
            return true;
        }

        // Check if user is attendee
        Optional<EventAttendee> attendee = eventAttendeeRepository
                .findByEventIdAndUserId(event.getId(), userId);
        return attendee.isPresent();
    }

    private boolean hasWritePermissionToEvent(Event event, Long userId) {
        // Creator has write permission
        if (event.getCreatedBy().getId().equals(userId)) {
            return true;
        }

        // Check if user has write permission through event share
        Optional<EventShare> eventShare = eventShareRepository
                .findByEventIdAndSharedWithId(event.getId(), userId);

        return eventShare.isPresent() && eventShare.get().getPermission() == EventShare.Permission.write;
    }
}