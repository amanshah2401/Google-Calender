package com.calendar.service;

import com.calendar.entity.Event;
import com.calendar.entity.EventRecurrence;
import com.calendar.repository.EventRecurrenceRepository;
import com.calendar.repository.EventRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// 2. EventRecurrenceService
@Service
@Transactional
public class EventRecurrenceService {

    @Autowired
    private EventRecurrenceRepository eventRecurrenceRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    public EventRecurrence createRecurrence(Long eventId, Long userId,
                                            EventRecurrence.RecurrenceType recurrenceType,
                                            Integer interval, LocalDate endDate,
                                            Integer occurrenceCount) {
        Event event = eventService.getEventById(eventId, userId);

        // Check if recurrence already exists
        Optional<EventRecurrence> existing = eventRecurrenceRepository.findByEventId(eventId);
        if (existing.isPresent()) {
            throw new IllegalStateException("Event already has recurrence rule");
        }

        EventRecurrence recurrence = new EventRecurrence(event, recurrenceType, interval);
        recurrence.setEndDate(endDate);
        recurrence.setOccurrenceCount(occurrenceCount);

        return eventRecurrenceRepository.save(recurrence);
    }

    public EventRecurrence updateRecurrence(Long eventId, Long userId,
                                            EventRecurrence.RecurrenceType recurrenceType,
                                            Integer interval, LocalDate endDate,
                                            Integer occurrenceCount) {
        Event event = eventService.getEventById(eventId, userId);

        EventRecurrence recurrence = eventRecurrenceRepository.findByEventId(eventId)
                .orElseThrow(() -> new RuntimeException("Recurrence not found"));

        recurrence.setRecurrenceType(recurrenceType);
        recurrence.setRecurrenceInterval(interval);
        recurrence.setEndDate(endDate);
        recurrence.setOccurrenceCount(occurrenceCount);

        return eventRecurrenceRepository.save(recurrence);
    }

    public void deleteRecurrence(Long eventId, Long userId) {
        eventService.getEventById(eventId, userId); // Verify access
        eventRecurrenceRepository.deleteByEventId(eventId);
    }

    public EventRecurrence getEventRecurrence(Long eventId, Long userId) {
        eventService.getEventById(eventId, userId); // Verify access
        return eventRecurrenceRepository.findByEventId(eventId)
                .orElseThrow(() -> new RuntimeException("No recurrence found for event"));
    }

    public List<Event> generateRecurringEventInstances(Long eventId, LocalDate fromDate, LocalDate toDate) {
        EventRecurrence recurrence = eventRecurrenceRepository.findByEventId(eventId)
                .orElseThrow(() -> new RuntimeException("No recurrence found"));

        Event masterEvent = recurrence.getEvent();
        List<Event> instances = new ArrayList<>();

        LocalDateTime currentStart = masterEvent.getStartTime();
        LocalDateTime currentEnd = masterEvent.getEndTime();
        Duration eventDuration = Duration.between(currentStart, currentEnd);

        int count = 0;
        LocalDate currentDate = fromDate;

        while (currentDate.isBefore(toDate) || currentDate.equals(toDate)) {
            if (recurrence.getEndDate() != null && currentDate.isAfter(recurrence.getEndDate())) {
                break;
            }

            if (recurrence.getOccurrenceCount() != null && count >= recurrence.getOccurrenceCount()) {
                break;
            }

            // Create event instance for this date
            LocalDateTime instanceStart = currentDate.atTime(currentStart.toLocalTime());
            LocalDateTime instanceEnd = instanceStart.plus(eventDuration);

            Event instance = new Event(
                    masterEvent.getTitle(),
                    masterEvent.getDescription(),
                    instanceStart,
                    instanceEnd,
                    masterEvent.getCalendar(),
                    masterEvent.getCreatedBy()
            );
            instance.setLocation(masterEvent.getLocation());
            instances.add(instance);

            // Calculate next occurrence date
            currentDate = getNextOccurrenceDate(currentDate, recurrence);
            count++;
        }

        return instances;
    }

    private LocalDate getNextOccurrenceDate(LocalDate currentDate, EventRecurrence recurrence) {
        switch (recurrence.getRecurrenceType()) {
            case daily:
                return currentDate.plusDays(recurrence.getRecurrenceInterval());
            case weekly:
                return currentDate.plusWeeks(recurrence.getRecurrenceInterval());
            case monthly:
                return currentDate.plusMonths(recurrence.getRecurrenceInterval());
            case yearly:
                return currentDate.plusYears(recurrence.getRecurrenceInterval());
            default:
                throw new IllegalArgumentException("Unknown recurrence type");
        }
    }
}
