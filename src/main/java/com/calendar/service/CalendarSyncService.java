package com.calendar.service;

import com.calendar.entity.Calendar;
import com.calendar.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// 6. CalendarSyncService (for external calendar integration)
@Service
public class CalendarSyncService {

    @Autowired
    private EventService eventService;

    @Autowired
    private CalendarService calendarService;

    // Placeholder for Google Calendar integration
    public void syncWithGoogleCalendar(Long userId, String googleCalendarId) {
        // Implementation would integrate with Google Calendar API
        System.out.println("Syncing with Google Calendar for user: " + userId);
        // ... Google Calendar API integration ...
    }

    // Placeholder for Outlook integration
    public void syncWithOutlookCalendar(Long userId, String outlookCalendarId) {
        // Implementation would integrate with Microsoft Graph API
        System.out.println("Syncing with Outlook Calendar for user: " + userId);
        // ... Microsoft Graph API integration ...
    }

    // Export calendar to ICS format
    public String exportCalendarToICS(Long calendarId, Long userId) {
        Calendar calendar = calendarService.getCalendarById(calendarId, userId);
        List<Event> events = eventService.getCalendarEvents(calendarId, userId);

        StringBuilder ics = new StringBuilder();
        ics.append("BEGIN:VCALENDAR\n");
        ics.append("VERSION:2.0\n");
        ics.append("PRODID:-//Calendar App//EN\n");

        for (Event event : events) {
            ics.append("BEGIN:VEVENT\n");
            ics.append("UID:").append(event.getId()).append("@calendarapp.com\n");
            ics.append("DTSTART:").append(formatDateTimeForICS(event.getStartTime())).append("\n");
            ics.append("DTEND:").append(formatDateTimeForICS(event.getEndTime())).append("\n");
            ics.append("SUMMARY:").append(event.getTitle()).append("\n");
            if (event.getDescription() != null) {
                ics.append("DESCRIPTION:").append(event.getDescription()).append("\n");
            }
            if (event.getLocation() != null) {
                ics.append("LOCATION:").append(event.getLocation()).append("\n");
            }
            ics.append("END:VEVENT\n");
        }

        ics.append("END:VCALENDAR\n");
        return ics.toString();
    }

    private String formatDateTimeForICS(LocalDateTime dateTime) {
        // Format: YYYYMMDDTHHMMSSZ
        return dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
    }
}
