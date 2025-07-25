package com.calendar.controller;

import com.calendar.config.JwtTokenProvider;
import com.calendar.dto.CreateCalendarRequest;
import com.calendar.dto.ShareCalendarRequest;
import com.calendar.dto.UpdateCalendarRequest;
import com.calendar.entity.Calendar;
import com.calendar.entity.CalendarShare;
import com.calendar.service.CalendarService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendars")
@CrossOrigin(origins = "*")
public class CalendarController {
    
    @Autowired
    private CalendarService calendarService;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @GetMapping
    public ResponseEntity<List<Calendar>> getUserCalendars(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<Calendar> calendars = calendarService.getUserCalendars(userId);
        return ResponseEntity.ok(calendars);
    }
    
    @PostMapping
    public ResponseEntity<Calendar> createCalendar(@RequestBody CreateCalendarRequest request,
                                                 HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Calendar calendar = calendarService.createCalendar(request.getName(), 
                                                          request.getDescription(),
                                                          request.getColor(), userId);
        return ResponseEntity.ok(calendar);
    }
    
    @GetMapping("/{calendarId}")
    public ResponseEntity<Calendar> getCalendar(@PathVariable Long calendarId,
                                              HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        Calendar calendar = calendarService.getCalendarById(calendarId, userId);
        return ResponseEntity.ok(calendar);
    }
    
    @PutMapping("/{calendarId}")
    public ResponseEntity<Calendar> updateCalendar(@PathVariable Long calendarId,
                                                 @RequestBody UpdateCalendarRequest request,
                                                 HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        Calendar calendar = calendarService.updateCalendar(calendarId, request.getName(),
                                                         request.getDescription(),
                                                         request.getColor(), userId);
        return ResponseEntity.ok(calendar);
    }
    
    @DeleteMapping("/{calendarId}")
    public ResponseEntity<String> deleteCalendar(@PathVariable Long calendarId,
                                                HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        calendarService.deleteCalendar(calendarId, userId);
        return ResponseEntity.ok("Calendar deleted successfully");
    }
    
    @PostMapping("/{calendarId}/share")
    public ResponseEntity<CalendarShare> shareCalendar(@PathVariable Long calendarId,
                                                       @RequestBody ShareCalendarRequest request,
                                                       HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        CalendarShare share = calendarService.shareCalendar(calendarId, request.getUserEmail(),
                                                          request.getPermission(), userId);
        return ResponseEntity.ok(share);
    }
    
    @DeleteMapping("/{calendarId}/share/{userId}")
    public ResponseEntity<String> removeCalendarShare(@PathVariable Long calendarId,
                                                     @PathVariable Long userId,
                                                     HttpServletRequest request) {
        Long ownerId = getUserIdFromRequest(request);
        calendarService.removeCalendarShare(calendarId, userId, ownerId);
        return ResponseEntity.ok("Calendar share removed successfully");
    }
    
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}

