package com.calendar.service;

import com.calendar.entity.Event;
import com.calendar.entity.EventComment;
import com.calendar.entity.User;
import com.calendar.exception.AccessDeniedException;
import com.calendar.repository.EventCommentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// 3. EventCommentService
@Service
@Transactional
public class EventCommentService {

    @Autowired
    private EventCommentRepository eventCommentRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    public EventComment addComment(Long eventId, Long userId, String comment) {
        Event event = eventService.getEventById(eventId, userId);
        User user = userService.findById(userId);

        EventComment eventComment = new EventComment(event, user, comment);
        return eventCommentRepository.save(eventComment);
    }

    public EventComment updateComment(Long commentId, Long userId, String comment) {
        EventComment eventComment = eventCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!eventComment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("No access to this comment");
        }

        eventComment.setComment(comment);
        return eventCommentRepository.save(eventComment);
    }

    public void deleteComment(Long commentId, Long userId) {
        EventComment eventComment = eventCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!eventComment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("No access to this comment");
        }

        eventCommentRepository.delete(eventComment);
    }

    public List<EventComment> getEventComments(Long eventId, Long userId) {
        // Verify access to event
        eventService.getEventById(eventId, userId);
        return eventCommentRepository.findByEventIdOrderByCreatedAtAsc(eventId);
    }

    public List<EventComment> getUserComments(Long userId) {
        return eventCommentRepository.findByUserId(userId);
    }

    public long getCommentCount(Long eventId, Long userId) {
        // Verify access to event
        eventService.getEventById(eventId, userId);
        return eventCommentRepository.countByEventId(eventId);
    }
}
