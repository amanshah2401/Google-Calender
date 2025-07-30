package com.calendar.controller;

import com.calendar.config.JwtTokenProvider;
import com.calendar.dto.*;
import com.calendar.entity.EventReminder;
import com.calendar.service.EventReminderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events/{eventId}/reminders")
@CrossOrigin(origins = "*")
public class EventReminderController {

    @Autowired
    private EventReminderService eventReminderService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<EventReminder> createReminder(@PathVariable Long eventId,
                                                        @RequestBody CreateReminderRequest request,
                                                        HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        EventReminder reminder = eventReminderService.createReminder(eventId, userId,
                request.getMinutesBefore(), request.getType());
        return ResponseEntity.ok(reminder);
    }

    @GetMapping
    public ResponseEntity<List<EventReminder>> getEventReminders(@PathVariable Long eventId,
                                                                 HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<EventReminder> reminders = eventReminderService.getEventReminders(eventId, userId);
        return ResponseEntity.ok(reminders);
    }

    @DeleteMapping("/{reminderId}")
    public ResponseEntity<String> deleteReminder(@PathVariable Long eventId,
                                                 @PathVariable Long reminderId,
                                                 HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        eventReminderService.deleteReminder(reminderId, userId);
        return ResponseEntity.ok("Reminder deleted successfully");
    }

    @GetMapping("/user")
    public ResponseEntity<List<EventReminder>> getUserReminders(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<EventReminder> reminders = eventReminderService.getUserReminders(userId);
        return ResponseEntity.ok(reminders);
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}

