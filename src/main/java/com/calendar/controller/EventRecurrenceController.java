package com.calendar.controller;

import com.calendar.config.JwtTokenProvider;
import com.calendar.dto.CreateRecurrenceRequest;
import com.calendar.dto.UpdateRecurrenceRequest;
import com.calendar.entity.Event;
import com.calendar.entity.EventRecurrence;
import com.calendar.service.EventRecurrenceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// 2. EventRecurrenceController
@RestController
@RequestMapping("/api/events/{eventId}/recurrence")
@CrossOrigin(origins = "*")
public class EventRecurrenceController {

    @Autowired
    private EventRecurrenceService eventRecurrenceService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<EventRecurrence> createRecurrence(@PathVariable Long eventId,
                                                            @RequestBody CreateRecurrenceRequest request,
                                                            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        EventRecurrence recurrence = eventRecurrenceService.createRecurrence(eventId, userId,
                request.getRecurrenceType(), request.getInterval(),
                request.getEndDate(), request.getOccurrenceCount());
        return ResponseEntity.ok(recurrence);
    }

    @GetMapping
    public ResponseEntity<EventRecurrence> getRecurrence(@PathVariable Long eventId,
                                                         HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        EventRecurrence recurrence = eventRecurrenceService.getEventRecurrence(eventId, userId);
        return ResponseEntity.ok(recurrence);
    }

    @PutMapping
    public ResponseEntity<EventRecurrence> updateRecurrence(@PathVariable Long eventId,
                                                            @RequestBody UpdateRecurrenceRequest request,
                                                            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        EventRecurrence recurrence = eventRecurrenceService.updateRecurrence(eventId, userId,
                request.getRecurrenceType(), request.getInterval(),
                request.getEndDate(), request.getOccurrenceCount());
        return ResponseEntity.ok(recurrence);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteRecurrence(@PathVariable Long eventId,
                                                   HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        eventRecurrenceService.deleteRecurrence(eventId, userId);
        return ResponseEntity.ok("Recurrence deleted successfully");
    }

    @GetMapping("/instances")
    public ResponseEntity<List<Event>> generateInstances(@PathVariable Long eventId,
                                                         @RequestParam String fromDate,
                                                         @RequestParam String toDate,
                                                         HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        LocalDate from = LocalDate.parse(fromDate);
        LocalDate to = LocalDate.parse(toDate);

        List<Event> instances = eventRecurrenceService.generateRecurringEventInstances(eventId, from, to);
        return ResponseEntity.ok(instances);
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}
