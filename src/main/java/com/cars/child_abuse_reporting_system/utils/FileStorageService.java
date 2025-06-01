package com.cars.child_abuse_reporting_system.utils;

import com.cars.child_abuse_reporting_system.enums.FileType;
import com.cars.child_abuse_reporting_system.exceptions.FileValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class FileStorageService {

    // Default image or placeholder URLs for different file types
    private static final String DEFAULT_IMAGE = "https://placeholder.com/default-image.jpg";
    private static final String DEFAULT_AUDIO = "https://placeholder.com/default-audio.mp3";
    private static final String DEFAULT_VIDEO = "https://placeholder.com/default-video.mp4";
    private static final String DEFAULT_DOCUMENT = "https://placeholder.com/default-document.pdf";

    // Maximum file sizes in bytes
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final long MAX_AUDIO_SIZE = 10 * 1024 * 1024; // 10MB
    private static final long MAX_VIDEO_SIZE = 50 * 1024 * 1024; // 50MB
    private static final long MAX_DOCUMENT_SIZE = 20 * 1024 * 1024; // 20MB

    // Allowed content types
    private static final Set<String> ALLOWED_IMAGE_TYPES = new HashSet<>(
            Arrays.asList("image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp"));

    // Audio types are now for reference only since we'll accept any type for voice recordings
    private static final Set<String> ALLOWED_AUDIO_TYPES = new HashSet<>(
            Arrays.asList("audio/mpeg", "audio/wav", "audio/ogg", "audio/x-m4a"));

    private static final Set<String> ALLOWED_VIDEO_TYPES = new HashSet<>(
            Arrays.asList("video/mp4", "video/mpeg", "video/quicktime", "video/x-msvideo", "video/webm"));

    private static final Set<String> ALLOWED_DOCUMENT_TYPES = new HashSet<>(
            Arrays.asList("application/pdf", "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "text/plain"));


    // For Cloudinary option
    @Autowired
    private CloudinaryServices cloudinaryService;

    /**
     * Processes an evidence image by validating and uploading it
     */
    public String processEvidenceFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return DEFAULT_DOCUMENT;
        }

        validateFile(file, FileType.DOCUMENT);
        return uploadFile(file);
    }

    /**
     * Processes a voice recording by validating and uploading it
     * Accepts any file type for voice recordings
     */
    public String processVoiceReport(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return DEFAULT_AUDIO;
        }

        // Only check file size for voice recordings, accepting any file type
        if (file.getSize() > MAX_AUDIO_SIZE) {
            throw new FileValidationException("Audio file size exceeds maximum allowed (10MB)");
        }

        return uploadFile(file);
    }

    /**
     * Processes a video report by validating and uploading it
     */
    public String processVideoReport(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return DEFAULT_VIDEO;
        }

        validateFile(file, FileType.VIDEO);
        return uploadFile(file);
    }

    /**
     * Validates a file based on its type, size, and content type
     */
    private void validateFile(MultipartFile file, FileType fileType) {
        // Check content type
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new FileValidationException("Cannot determine file type. Please ensure the file has a valid extension.");
        }

        switch (fileType) {
            case IMAGE:
                if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
                    throw new FileValidationException("Invalid image format. Allowed formats: JPG, PNG, GIF, BMP, WebP");
                }
                if (file.getSize() > MAX_IMAGE_SIZE) {
                    throw new FileValidationException("Image file size exceeds maximum allowed (5MB)");
                }
                break;

            case AUDIO:
                // No content type validation for audio files
                // Only check file size
                if (file.getSize() > MAX_AUDIO_SIZE) {
                    throw new FileValidationException("Audio file size exceeds maximum allowed (10MB)");
                }
                break;

            case VIDEO:
                if (!ALLOWED_VIDEO_TYPES.contains(contentType)) {
                    throw new FileValidationException("Invalid video format. Allowed formats: MP4, MPEG, MOV, AVI, WebM");
                }
                if (file.getSize() > MAX_VIDEO_SIZE) {
                    throw new FileValidationException("Video file size exceeds maximum allowed (50MB)");
                }
                break;

            case DOCUMENT:
                if (!ALLOWED_DOCUMENT_TYPES.contains(contentType)) {
                    throw new FileValidationException("Invalid document format. Allowed formats: PDF, DOC, DOCX, XLS, XLSX, TXT");
                }
                if (file.getSize() > MAX_DOCUMENT_SIZE) {
                    throw new FileValidationException("Document file size exceeds maximum allowed (20MB)");
                }
                break;
        }
    }

    /**
     * Uploads a file to the configured storage service (AWS S3)
     */
    private String uploadFile(MultipartFile file) {
        try {
            // Uncomment if you want to use Cloudinary instead of AWS S3
            return cloudinaryService.uploadFile(file);
        } catch (Exception e) {
            throw new FileValidationException("Failed to upload file: " + e.getMessage());
        }
    }
}


