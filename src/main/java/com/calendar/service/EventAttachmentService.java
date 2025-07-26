package com.calendar.service;

import com.calendar.entity.Event;
import com.calendar.entity.EventAttachment;
import com.calendar.entity.User;
import com.calendar.exception.AccessDeniedException;
import com.calendar.repository.EventAttachmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// 4. EventAttachmentService
@Service
@Transactional
public class EventAttachmentService {

    @Autowired
    private EventAttachmentRepository eventAttachmentRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    // This would typically integrate with a file storage service (AWS S3, local storage, etc.)
    public EventAttachment uploadAttachment(Long eventId, Long userId, String fileName,
                                            String contentType, Long fileSize, byte[] fileData) {
        Event event = eventService.getEventById(eventId, userId);
        User user = userService.findById(userId);

        // Save file to storage (implementation depends on storage solution)
        String filePath = saveFileToStorage(fileName, fileData);

        EventAttachment attachment = new EventAttachment(event, user, fileName, filePath, contentType, fileSize);
        return eventAttachmentRepository.save(attachment);
    }

    public void deleteAttachment(Long attachmentId, Long userId) {
        EventAttachment attachment = eventAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        // Verify access to the event
        eventService.getEventById(attachment.getEvent().getId(), userId);

        // Check if user can delete (owner or uploader)
        if (!attachment.getUploadedBy().getId().equals(userId) &&
                !attachment.getEvent().getCreatedBy().getId().equals(userId)) {
            throw new AccessDeniedException("No permission to delete this attachment");
        }

        // Delete file from storage
        deleteFileFromStorage(attachment.getFilePath());

        eventAttachmentRepository.delete(attachment);
    }

    public List<EventAttachment> getEventAttachments(Long eventId, Long userId) {
        // Verify access to event
        eventService.getEventById(eventId, userId);
        return eventAttachmentRepository.findByEventId(eventId);
    }

    public EventAttachment getAttachment(Long attachmentId, Long userId) {
        EventAttachment attachment = eventAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        // Verify access to the event
        eventService.getEventById(attachment.getEvent().getId(), userId);

        return attachment;
    }

    public byte[] downloadAttachment(Long attachmentId, Long userId) {
        EventAttachment attachment = getAttachment(attachmentId, userId);

        // Load file from storage
        return loadFileFromStorage(attachment.getFilePath());
    }

    public Long getUserStorageUsage(Long userId) {
        Long usage = eventAttachmentRepository.getTotalStorageUsedByUser(userId);
        return usage != null ? usage : 0L;
    }

    public Long getEventStorageUsage(Long eventId, Long userId) {
        // Verify access to event
        eventService.getEventById(eventId, userId);

        Long usage = eventAttachmentRepository.getTotalStorageUsedByEvent(eventId);
        return usage != null ? usage : 0L;
    }

    // Placeholder methods for file storage operations
    private String saveFileToStorage(String fileName, byte[] fileData) {
        // Implementation depends on storage solution
        // Could save to local filesystem, AWS S3, Google Cloud Storage, etc.
        String filePath = "/uploads/" + System.currentTimeMillis() + "_" + fileName;
        // ... save file logic ...
        return filePath;
    }

    private void deleteFileFromStorage(String filePath) {
        // Implementation depends on storage solution
        // ... delete file logic ...
    }

    private byte[] loadFileFromStorage(String filePath) {
        // Implementation depends on storage solution
        // ... load file logic ...
        return new byte[0]; // placeholder
    }
}
