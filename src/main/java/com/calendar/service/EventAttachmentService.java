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

    @Autowired
    private FileStorageService fileStorageService;

    // Upload attachment using MongoDB GridFS
    public EventAttachment uploadAttachment(Long eventId, Long userId, String fileName,
                                            String contentType, Long fileSize, byte[] fileData) {
        Event event = eventService.getEventById(eventId, userId);
        User user = userService.findById(userId);

        // Store file in MongoDB GridFS
        String fileId = fileStorageService.storeFile(fileName, contentType, fileData);

        EventAttachment attachment = new EventAttachment(event, user, fileName, fileId, contentType, fileSize);
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

        // Delete file from MongoDB GridFS
        fileStorageService.deleteFile(attachment.getFileId());

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

        // Load file from MongoDB GridFS
        return fileStorageService.retrieveFile(attachment.getFileId());
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

    /**
     * Get file metadata without downloading the actual file
     */
    public EventAttachment getAttachmentMetadata(Long attachmentId, Long userId) {
        EventAttachment attachment = getAttachment(attachmentId, userId);
        
        // Verify file exists in MongoDB
        if (!fileStorageService.fileExists(attachment.getFileId())) {
            throw new RuntimeException("File not found in storage: " + attachment.getFileId());
        }
        
        return attachment;
    }

    /**
     * Check if file exists in storage
     */
    public boolean fileExists(Long attachmentId, Long userId) {
        try {
            EventAttachment attachment = getAttachment(attachmentId, userId);
            return fileStorageService.fileExists(attachment.getFileId());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get file size from MongoDB
     */
    public Long getFileSize(Long attachmentId, Long userId) {
        EventAttachment attachment = getAttachment(attachmentId, userId);
        
        // Get file metadata from MongoDB
        var gridFSFile = fileStorageService.getFileMetadata(attachment.getFileId());
        return gridFSFile != null ? gridFSFile.getLength() : 0L;
    }

    // File storage operations are now handled by FileStorageService
    // which uses MongoDB GridFS for efficient file storage
}
