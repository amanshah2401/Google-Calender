package com.calendar.controller;

import com.calendar.config.JwtTokenProvider;
import com.calendar.dto.CreateEventRequest;
import com.calendar.dto.UpdateEventRequest;
import com.calendar.entity.Event;
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
    
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}

