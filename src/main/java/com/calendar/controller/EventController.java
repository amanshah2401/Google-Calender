package com.calendar.controller;

import com.calendar.config.JwtTokenProvider;
import com.calendar.dto.*;
import com.calendar.entity.Event;
import com.calendar.entity.EventAttendee;
import com.calendar.entity.EventShare;
import com.calendar.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody CreateEventRequest request,
                                             HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Event event = eventService.createEvent(request.getTitle(), request.getDescription(),
                request.getStartTime(), request.getEndTime(),
                request.getLocation(), request.getCalendarId(), userId);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Event> getEvent(@PathVariable Long eventId,
                                          HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        Event event = eventService.getEventById(eventId, userId);
        return ResponseEntity.ok(event);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long eventId,
                                             @RequestBody UpdateEventRequest request,
                                             HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Event event = eventService.updateEvent(eventId, request.getTitle(),
                request.getDescription(), request.getStartTime(),
                request.getEndTime(), request.getLocation(), userId);
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long eventId,
                                              HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        eventService.deleteEvent(eventId, userId);
        return ResponseEntity.ok("Event deleted successfully");
    }

    @GetMapping("/calendar/{calendarId}")
    public ResponseEntity<List<Event>> getCalendarEvents(@PathVariable Long calendarId,
                                                         HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<Event> events = eventService.getCalendarEvents(calendarId, userId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/my-events")
    public ResponseEntity<List<Event>> getUserEvents(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<Event> events = eventService.getUserEvents(userId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/range")
    public ResponseEntity<List<Event>> getEventsInRange(@RequestParam List<Long> calendarIds,
                                                        @RequestParam String start,
                                                        @RequestParam String end,
                                                        HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        LocalDateTime startTime = LocalDateTime.parse(start);
        LocalDateTime endTime = LocalDateTime.parse(end);

        List<Event> events = eventService.getEventsInDateRange(calendarIds, startTime, endTime, userId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/today")
    public ResponseEntity<List<Event>> getTodayEvents(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<Event> events = eventService.getTodayEvents(userId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Event>> searchEvents(@RequestParam String query,
                                                    HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<Event> events = eventService.searchEvents(query, userId);
        return ResponseEntity.ok(events);
    }

    // Event Sharing APIs
    @PostMapping("/{eventId}/share")
    public ResponseEntity<EventShare> shareEvent(@PathVariable Long eventId,
                                                 @RequestBody ShareEventRequest request,
                                                 HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        EventShare share = eventService.shareEvent(eventId, request.getUserEmail(),
                request.getPermission(), userId);
        return ResponseEntity.ok(share);
    }

    @DeleteMapping("/{eventId}/share/{userId}")
    public ResponseEntity<String> removeEventShare(@PathVariable Long eventId,
                                                   @PathVariable Long userId,
                                                   HttpServletRequest request) {
        Long organizerId = getUserIdFromRequest(request);
        eventService.removeEventShare(eventId, userId, organizerId);
        return ResponseEntity.ok("Event share removed successfully");
    }

    // Event Invitation APIs
    @PostMapping("/{eventId}/invite")
    public ResponseEntity<EventAttendee> inviteUser(@PathVariable Long eventId,
                                                    @RequestBody InviteUserRequest request,
                                                    HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        EventAttendee attendee = eventService.inviteUserToEvent(eventId, request.getUserEmail(), userId);
        return ResponseEntity.ok(attendee);
    }

    @PutMapping("/{eventId}/respond")
    public ResponseEntity<EventAttendee> respondToInvitation(@PathVariable Long eventId,
                                                             @RequestBody RespondToInvitationRequest request,
                                                             HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        EventAttendee attendee = eventService.respondToEventInvitation(eventId, userId, request.getResponse());
        return ResponseEntity.ok(attendee);
    }

    @GetMapping("/{eventId}/attendees")
    public ResponseEntity<List<EventAttendee>> getEventAttendees(@PathVariable Long eventId,
                                                                 HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<EventAttendee> attendees = eventService.getEventAttendees(eventId, userId);
        return ResponseEntity.ok(attendees);
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}

