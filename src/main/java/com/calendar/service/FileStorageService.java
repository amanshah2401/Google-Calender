package com.calendar.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FileStorageService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    /**
     * Store file in MongoDB GridFS
     * @param fileName Original file name
     * @param contentType MIME type
     * @param fileData File bytes
     * @return ObjectId of stored file
     */
    public String storeFile(String fileName, String contentType, byte[] fileData) {
        try {
            // Create metadata
            Document metadata = new Document()
                    .append("contentType", contentType)
                    .append("originalFileName", fileName)
                    .append("uploadedAt", System.currentTimeMillis());

            // Upload options
            GridFSUploadOptions uploadOptions = new GridFSUploadOptions()
                    .metadata(metadata);

            // Upload file
            ObjectId fileId = gridFSBucket.uploadFromStream(
                    fileName,
                    new ByteArrayInputStream(fileData),
                    uploadOptions
            );

            return fileId.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file: " + fileName, e);
        }
    }

        /**
     * Retrieve file from MongoDB GridFS
     * @param fileId ObjectId of the file
     * @return File bytes
     */
    public byte[] retrieveFile(String fileId) {
        ObjectId objectId = new ObjectId(fileId);

        // Download stream
        GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(objectId);

        // Read to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = downloadStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        downloadStream.close();
        return outputStream.toByteArray();

    }

    /**
     * Get file metadata
     * @param fileId ObjectId of the file
     * @return File metadata
     */
    public GridFSFile getFileMetadata(String fileId) {
        try {
            ObjectId objectId = new ObjectId(fileId);
            return gridFSBucket.find().filter(new Document("_id", objectId)).first();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get file metadata: " + fileId, e);
        }
    }

    /**
     * Delete file from MongoDB GridFS
     * @param fileId ObjectId of the file
     */
    public void deleteFile(String fileId) {
        try {
            ObjectId objectId = new ObjectId(fileId);
            gridFSBucket.delete(objectId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file: " + fileId, e);
        }
    }

    /**
     * Check if file exists
     * @param fileId ObjectId of the file
     * @return true if file exists
     */
    public boolean fileExists(String fileId) {
        try {
            ObjectId objectId = new ObjectId(fileId);
            return gridFSBucket.find().filter(new Document("_id", objectId)).first() != null;
        } catch (Exception e) {
            return false;
        }
    }
} 