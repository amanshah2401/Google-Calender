package com.calendar.repository;

import com.calendar.entity.EventAttachment;
import jakarta.validation.constraints.Future;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Missing Repository 4: EventAttachmentRepository
@Repository
public interface EventAttachmentRepository extends JpaRepository<EventAttachment, Long> {
    List<EventAttachment> findByEventId(Long eventId);

    List<EventAttachment> findByUploadedById(Long uploadedById);

    List<EventAttachment> findByEventIdAndUploadedById(Long eventId, Long uploadedById);

    // Find attachments by content type
    List<EventAttachment> findByContentTypeStartingWith(String contentType);

    // Find large attachments
    @Query("SELECT ea FROM EventAttachment ea WHERE ea.fileSize > :sizeLimit")
    List<EventAttachment> findLargeAttachments(@Param("sizeLimit") Long sizeLimit);

    // Get total storage used by user
    @Query("SELECT SUM(ea.fileSize) FROM EventAttachment ea WHERE ea.uploadedBy.id = :userId")
    Long getTotalStorageUsedByUser(@Param("userId") Long userId);

    // Get total storage used for an event
    @Query("SELECT SUM(ea.fileSize) FROM EventAttachment ea WHERE ea.event.id = :eventId")
    Long getTotalStorageUsedByEvent(@Param("eventId") Long eventId);
}
