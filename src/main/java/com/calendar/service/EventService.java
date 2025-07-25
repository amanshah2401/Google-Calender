package com.calendar.service;

import com.calendar.entity.Calendar;
import com.calendar.entity.Event;
import com.calendar.entity.User;
import com.calendar.exception.EventNotFoundException;
import com.calendar.exception.InvalidEventTimeException;
import com.calendar.repository.EventRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

    public Event createEvent(String title, String description, LocalDateTime startTime,
                             LocalDateTime endTime, String location, Long calendarId, Long userId) {
        if (startTime.isAfter(endTime)) {
            throw new InvalidEventTimeException("Start time must be before end time");
        }

        Calendar calendar = calendarService.getCalendarById(calendarId, userId);
        User user = userService.findById(userId);

        Event event = new Event(title, description, startTime, endTime, calendar, user);
        event.setLocation(location);

        return eventRepository.save(event);
    }

    public List<Event> getCalendarEvents(Long calendarId, Long userId) {
        // Verify access to calendar
        calendarService.getCalendarById(calendarId, userId);
        return eventRepository.findByCalendarId(calendarId);
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

        // Verify access to calendar
        calendarService.getCalendarById(event.getCalendar().getId(), userId);

        return event;
    }

    public Event updateEvent(Long eventId, String title, String description,
                             LocalDateTime startTime, LocalDateTime endTime,
                             String location, Long userId) {
        Event event = getEventById(eventId, userId);

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
        eventRepository.delete(event);
    }

    public List<Event> searchEvents(String query, Long userId) {
        List<Calendar> userCalendars = calendarService.getUserCalendars(userId);
        List<Long> calendarIds = userCalendars.stream()
                .map(Calendar::getId)
                .collect(Collectors.toList());

        return eventRepository.searchEvents(calendarIds, query);
    }

    public List<Event> getTodayEvents(Long userId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);

        List<Calendar> userCalendars = calendarService.getUserCalendars(userId);
        List<Long> calendarIds = userCalendars.stream()
                .map(Calendar::getId)
                .collect(Collectors.toList());

        return eventRepository.findEventsByCalendarsAndDateRange(calendarIds, startOfDay, endOfDay);
    }
}