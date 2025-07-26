package com.calendar.service;

import com.calendar.entity.Calendar;
import com.calendar.entity.Event;
import com.calendar.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 7. EventAnalyticsService (for reporting and analytics)
@Service
public class EventAnalyticsService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CalendarService calendarService;

    public Map<String, Object> getUserEventStatistics(Long userId) {
        List<Calendar> userCalendars = calendarService.getUserCalendars(userId);
        List<Long> calendarIds = userCalendars.stream()
                .map(Calendar::getId)
                .collect(Collectors.toList());

        Map<String, Object> stats = new HashMap<>();

        // Total events
        LocalDateTime startOfYear = LocalDateTime.of(LocalDate.now().getYear(), 1, 1, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(LocalDate.now().getYear(), 12, 31, 23, 59);

        long totalEvents = eventRepository.countEventsByCalendarsAndDateRange(
                calendarIds, startOfYear, endOfYear);
        stats.put("totalEventsThisYear", totalEvents);

        // Events this month
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().withDayOfMonth(
                LocalDate.now().lengthOfMonth()).atTime(23, 59);

        long monthlyEvents = eventRepository.countEventsByCalendarsAndDateRange(
                calendarIds, startOfMonth, endOfMonth);
        stats.put("eventsThisMonth", monthlyEvents);

        // Events this week
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        long weeklyEvents = eventRepository.countEventsByCalendarsAndDateRange(
                calendarIds, startOfWeek.atStartOfDay(), endOfWeek.atTime(23, 59));
        stats.put("eventsThisWeek", weeklyEvents);

        // Most active calendar
        Map<String, Long> calendarEventCounts = new HashMap<>();
        for (Calendar calendar : userCalendars) {
            long count = eventRepository.countEventsByCalendarsAndDateRange(
                    Arrays.asList(calendar.getId()), startOfYear, endOfYear);
            calendarEventCounts.put(calendar.getName(), count);
        }
        stats.put("calendarEventCounts", calendarEventCounts);

        return stats;
    }

    public List<Event> getUpcomingEvents(Long userId, int limit) {
        List<Calendar> userCalendars = calendarService.getUserCalendars(userId);
        List<Long> calendarIds = userCalendars.stream()
                .map(Calendar::getId)
                .collect(Collectors.toList());

        return eventRepository.findUpcomingEvents(calendarIds, LocalDateTime.now(), limit);
    }

    public Map<String, Integer> getEventsByDayOfWeek(Long userId) {
        List<Calendar> userCalendars = calendarService.getUserCalendars(userId);
        List<Long> calendarIds = userCalendars.stream()
                .map(Calendar::getId)
                .collect(Collectors.toList());

        LocalDateTime startOfYear = LocalDateTime.of(LocalDate.now().getYear(), 1, 1, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(LocalDate.now().getYear(), 12, 31, 23, 59);

        List<Event> events = eventRepository.findEventsByCalendarsAndDateRange(
                calendarIds, startOfYear, endOfYear);

        Map<String, Integer> dayCount = new HashMap<>();
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};

        for (String day : days) {
            dayCount.put(day, 0);
        }

        for (Event event : events) {
            String dayOfWeek = event.getStartTime().getDayOfWeek().name();
            dayCount.put(dayOfWeek, dayCount.get(dayOfWeek) + 1);
        }

        return dayCount;
    }

    public Map<String, Integer> getEventsByHourOfDay(Long userId) {
        List<Calendar> userCalendars = calendarService.getUserCalendars(userId);
        List<Long> calendarIds = userCalendars.stream()
                .map(Calendar::getId)
                .collect(Collectors.toList());

        LocalDateTime startOfYear = LocalDateTime.of(LocalDate.now().getYear(), 1, 1, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(LocalDate.now().getYear(), 12, 31, 23, 59);

        List<Event> events = eventRepository.findEventsByCalendarsAndDateRange(
                calendarIds, startOfYear, endOfYear);

        Map<String, Integer> hourCount = new HashMap<>();
        for (int i = 0; i < 24; i++) {
            hourCount.put(String.format("%02d:00", i), 0);
        }

        for (Event event : events) {
            String hour = String.format("%02d:00", event.getStartTime().getHour());
            hourCount.put(hour, hourCount.get(hour) + 1);
        }

        return hourCount;
    }
}
