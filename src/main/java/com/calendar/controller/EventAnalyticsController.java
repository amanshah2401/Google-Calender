package com.calendar.controller;

import com.calendar.config.JwtTokenProvider;
import com.calendar.entity.Event;
import com.calendar.service.EventAnalyticsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class EventAnalyticsController {

    @Autowired
    private EventAnalyticsService eventAnalyticsService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/user-statistics")
    public ResponseEntity<Map<String, Object>> getUserStatistics(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        Map<String, Object> stats = eventAnalyticsService.getUserEventStatistics(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/upcoming-events")
    public ResponseEntity<List<Event>> getUpcomingEvents(@RequestParam(defaultValue = "10") int limit,
                                                         HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<Event> events = eventAnalyticsService.getUpcomingEvents(userId, limit);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events-by-day")
    public ResponseEntity<Map<String, Integer>> getEventsByDayOfWeek(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        Map<String, Integer> dayStats = eventAnalyticsService.getEventsByDayOfWeek(userId);
        return ResponseEntity.ok(dayStats);
    }

    @GetMapping("/events-by-hour")
    public ResponseEntity<Map<String, Integer>> getEventsByHourOfDay(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        Map<String, Integer> hourStats = eventAnalyticsService.getEventsByHourOfDay(userId);
        return ResponseEntity.ok(hourStats);
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}
