package com.calendar.controller;

import com.calendar.config.JwtTokenProvider;
import com.calendar.dto.GoogleSyncRequest;
import com.calendar.dto.OutlookSyncRequest;
import com.calendar.service.CalendarSyncService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calendars/sync")
@CrossOrigin(origins = "*")
public class CalendarSyncController {

    @Autowired
    private CalendarSyncService calendarSyncService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/google/{calendarId}")
    public ResponseEntity<String> syncWithGoogle(@PathVariable Long calendarId,
                                                 @RequestBody GoogleSyncRequest request,
                                                 HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        calendarSyncService.syncWithGoogleCalendar(userId, request.getGoogleCalendarId());
        return ResponseEntity.ok("Google Calendar sync initiated");
    }

    @PostMapping("/outlook/{calendarId}")
    public ResponseEntity<String> syncWithOutlook(@PathVariable Long calendarId,
                                                  @RequestBody OutlookSyncRequest request,
                                                  HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        calendarSyncService.syncWithOutlookCalendar(userId, request.getOutlookCalendarId());
        return ResponseEntity.ok("Outlook Calendar sync initiated");
    }

    @GetMapping("/{calendarId}/export")
    public ResponseEntity<String> exportCalendar(@PathVariable Long calendarId,
                                                 HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        String icsContent = calendarSyncService.exportCalendarToICS(calendarId, userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("calendar.ics").build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(icsContent);
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}
