package com.calendar.controller;

import com.calendar.config.JwtTokenProvider;
import com.calendar.dto.AddCommentRequest;
import com.calendar.dto.UpdateCommentRequest;
import com.calendar.entity.EventComment;
import com.calendar.service.EventCommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 3. EventCommentController
@RestController
@RequestMapping("/api/events/{eventId}/comments")
@CrossOrigin(origins = "*")
public class EventCommentController {

    @Autowired
    private EventCommentService eventCommentService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<EventComment> addComment(@PathVariable Long eventId,
                                                   @RequestBody AddCommentRequest request,
                                                   HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        EventComment comment = eventCommentService.addComment(eventId, userId, request.getComment());
        return ResponseEntity.ok(comment);
    }

    @GetMapping
    public ResponseEntity<List<EventComment>> getComments(@PathVariable Long eventId,
                                                          HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<EventComment> comments = eventCommentService.getEventComments(eventId, userId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<EventComment> updateComment(@PathVariable Long eventId,
                                                      @PathVariable Long commentId,
                                                      @RequestBody UpdateCommentRequest request,
                                                      HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        EventComment comment = eventCommentService.updateComment(commentId, userId, request.getComment());
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long eventId,
                                                @PathVariable Long commentId,
                                                HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        eventCommentService.deleteComment(commentId, userId);
        return ResponseEntity.ok("Comment deleted successfully");
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getCommentCount(@PathVariable Long eventId,
                                                HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        long count = eventCommentService.getCommentCount(eventId, userId);
        return ResponseEntity.ok(count);
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}
