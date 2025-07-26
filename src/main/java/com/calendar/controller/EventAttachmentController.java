package com.calendar.controller;

import com.calendar.config.JwtTokenProvider;
import com.calendar.entity.EventAttachment;
import com.calendar.service.EventAttachmentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

// 4. EventAttachmentController
@RestController
@RequestMapping("/api/events/{eventId}/attachments")
@CrossOrigin(origins = "*")
public class EventAttachmentController {

    @Autowired
    private EventAttachmentService eventAttachmentService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<EventAttachment> uploadAttachment(@PathVariable Long eventId,
                                                            @RequestParam("file") MultipartFile file,
                                                            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);

        try {
            EventAttachment attachment = eventAttachmentService.uploadAttachment(
                    eventId, userId, file.getOriginalFilename(),
                    file.getContentType(), file.getSize(), file.getBytes());
            return ResponseEntity.ok(attachment);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<EventAttachment>> getAttachments(@PathVariable Long eventId,
                                                                HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<EventAttachment> attachments = eventAttachmentService.getEventAttachments(eventId, userId);
        return ResponseEntity.ok(attachments);
    }

    @GetMapping("/{attachmentId}")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long eventId,
                                                     @PathVariable Long attachmentId,
                                                     HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);

        EventAttachment attachment = eventAttachmentService.getAttachment(attachmentId, userId);
        byte[] fileData = eventAttachmentService.downloadAttachment(attachmentId, userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(attachment.getContentType()));
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(attachment.getFileName()).build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileData);
    }

    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<String> deleteAttachment(@PathVariable Long eventId,
                                                   @PathVariable Long attachmentId,
                                                   HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        eventAttachmentService.deleteAttachment(attachmentId, userId);
        return ResponseEntity.ok("Attachment deleted successfully");
    }

    @GetMapping("/storage-usage")
    public ResponseEntity<Long> getStorageUsage(@PathVariable Long eventId,
                                                HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        Long usage = eventAttachmentService.getEventStorageUsage(eventId, userId);
        return ResponseEntity.ok(usage);
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}
