package com.calendar.service;

import com.calendar.entity.Event;
import com.calendar.entity.EventReminder;
import com.calendar.entity.User;
import com.calendar.exception.AccessDeniedException;
import com.calendar.repository.EventReminderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// 1. EventReminderService
@Service
@Transactional
public class EventReminderService {

    @Autowired
    private EventReminderRepository eventReminderRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    public EventReminder createReminder(Long eventId, Long userId, Integer minutesBefore,
                                        EventReminder.ReminderType type) {
        Event event = eventService.getEventById(eventId, userId);
        User user = userService.findById(userId);

        EventReminder reminder = new EventReminder(event, user, minutesBefore, type);
        return eventReminderRepository.save(reminder);
    }

    public List<EventReminder> getEventReminders(Long eventId, Long userId) {
        // Verify access to event
        eventService.getEventById(eventId, userId);
        return eventReminderRepository.findByEventIdAndUserId(eventId, userId);
    }

    public List<EventReminder> getUserReminders(Long userId) {
        return eventReminderRepository.findByUserId(userId);
    }

    public void deleteReminder(Long reminderId, Long userId) {
        EventReminder reminder = eventReminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        if (!reminder.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("No access to this reminder");
        }

        eventReminderRepository.delete(reminder);
    }

    public List<EventReminder> getPendingReminders() {
        return eventReminderRepository.findPendingReminders(LocalDateTime.now());
    }

    public void markReminderAsSent(Long reminderId) {
        EventReminder reminder = eventReminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        reminder.setIsSent(true);
        eventReminderRepository.save(reminder);
    }
}

