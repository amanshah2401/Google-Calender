package com.calendar.service;

import com.calendar.entity.Calendar;
import com.calendar.entity.CalendarShare;
import com.calendar.entity.User;
import com.calendar.exception.AccessDeniedException;
import com.calendar.exception.CalendarNotFoundException;
import com.calendar.repository.CalendarRepository;
import com.calendar.repository.CalendarShareRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CalendarService {

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private CalendarShareRepository calendarShareRepository;

    @Autowired
    private UserService userService;

    public Calendar createCalendar(String name, String description, String color, Long ownerId) {
        User owner = userService.findById(ownerId);
        Calendar calendar = new Calendar(name, description, owner);
        if (color != null && !color.isEmpty()) {
            calendar.setColor(color);
        }
        return calendarRepository.save(calendar);
    }

    public List<Calendar> getUserCalendars(Long userId) {
        return calendarRepository.findAccessibleCalendars(userId);
    }

    public Calendar getCalendarById(Long calendarId, Long userId) {
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new CalendarNotFoundException("Calendar not found: " + calendarId));

        if (!hasAccessToCalendar(calendar, userId)) {
            throw new AccessDeniedException("No access to calendar: " + calendarId);
        }

        return calendar;
    }

    public Calendar updateCalendar(Long calendarId, String name, String description,
                                   String color, Long userId) {
        Calendar calendar = getCalendarById(calendarId, userId);

        if (!hasWritePermission(calendar, userId)) {
            throw new AccessDeniedException("No write permission for calendar: " + calendarId);
        }

        calendar.setName(name);
        calendar.setDescription(description);
        if (color != null) {
            calendar.setColor(color);
        }

        return calendarRepository.save(calendar);
    }

    public void deleteCalendar(Long calendarId, Long userId) {
        Calendar calendar = getCalendarById(calendarId, userId);

        if (!calendar.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Only owner can delete calendar: " + calendarId);
        }

        calendarRepository.delete(calendar);
    }

    public CalendarShare shareCalendar(Long calendarId, String userEmail,
                                       CalendarShare.Permission permission, Long ownerId) {
        Calendar calendar = getCalendarById(calendarId, ownerId);

        if (!calendar.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Only owner can share calendar: " + calendarId);
        }

        User userToShareWith = userService.findByEmail(userEmail);

        // Check if already shared
        Optional<CalendarShare> existingShare = calendarShareRepository
                .findByCalendarIdAndSharedWithId(calendarId, userToShareWith.getId());

        if (existingShare.isPresent()) {
            CalendarShare share = existingShare.get();
            share.setPermission(permission);
            return calendarShareRepository.save(share);
        }

        CalendarShare newShare = new CalendarShare(calendar, userToShareWith, permission);
        return calendarShareRepository.save(newShare);
    }

    public void removeCalendarShare(Long calendarId, Long userIdToRemove, Long ownerId) {
        Calendar calendar = getCalendarById(calendarId, ownerId);

        if (!calendar.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Only owner can remove shares: " + calendarId);
        }

        calendarShareRepository.deleteByCalendarIdAndSharedWithId(calendarId, userIdToRemove);
    }

    private boolean hasAccessToCalendar(Calendar calendar, Long userId) {
        // Owner has access
        if (calendar.getOwner().getId().equals(userId)) {
            return true;
        }

        // Check if shared
        return calendarShareRepository.findByCalendarIdAndSharedWithId(
                calendar.getId(), userId).isPresent();
    }

    private boolean hasWritePermission(Calendar calendar, Long userId) {
        // Owner has write permission
        if (calendar.getOwner().getId().equals(userId)) {
            return true;
        }

        // Check shared permission
        Optional<CalendarShare> share = calendarShareRepository
                .findByCalendarIdAndSharedWithId(calendar.getId(), userId);

        return share.isPresent() && share.get().getPermission() == CalendarShare.Permission.write;
    }
}