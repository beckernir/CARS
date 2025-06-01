package com.cars.child_abuse_reporting_system.utils;

import com.cars.child_abuse_reporting_system.exceptions.FileProcessingException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryServices {

    private static final Logger logger = LoggerFactory.getLogger(CloudinaryServices.class);
    private final Cloudinary cloudinary;

    public CloudinaryServices(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Uploads a file to Cloudinary
     * Enhanced to handle voice recordings (WebM format) and provide detailed logging
     */
    public String uploadFile(MultipartFile file) {
        Path tempFile = null;
        try {
            // Log incoming file details
            logger.info("Processing file upload: name={}, contentType={}, size={}",
                    file.getOriginalFilename(), file.getContentType(), file.getSize());

            // Create a safe extension based on content type
            String fileExtension = getExtensionFromContentType(file.getContentType(), file.getOriginalFilename());

            // Create a temporary file with UUID name to avoid special character issues
            String tempFileName = UUID.randomUUID().toString() + fileExtension;

            // Create temporary file using Java NIO for better cross-platform compatibility
            tempFile = Files.createTempFile("upload_", tempFileName);
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            logger.info("Created temporary file: {}", tempFile);

            // Special handling for voice recordings which might be WebM format
            Map<String, Object> params = ObjectUtils.asMap(
                    "resource_type", "auto",  // Auto-detect resource type
                    "use_filename", false,    // Don't use original filename
                    "unique_filename", true,  // Generate unique name on Cloudinary
                    "overwrite", true         // Overwrite if exists
            );

            // If it's a voice recording (likely WebM), explicitly set resource type
            if (file.getContentType() != null &&
                    (file.getContentType().contains("webm") || file.getOriginalFilename() != null && file.getOriginalFilename().contains("webm"))) {
                params.put("resource_type", "video"); // WebM needs to be treated as video type in Cloudinary
                logger.info("Detected WebM format, setting resource_type to video");
            }

            // Upload to Cloudinary with detailed logging
            logger.info("Uploading to Cloudinary with params: {}", params);
            Map<String, Object> uploadResult = cloudinary.uploader().upload(tempFile.toFile(), params);

            // Log success details
            logger.info("Upload successful: resultId={}, format={}",
                    uploadResult.get("public_id"), uploadResult.get("format"));

            // Return the secure URL
            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            logger.error("IO error during file processing", e);
            throw new FileProcessingException("Failed to process file for upload: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error during Cloudinary upload", e);
            throw new FileProcessingException("Failed during upload process: " + e.getMessage(), e);
        } finally {
            // Clean up the temp file
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                    logger.info("Deleted temporary file: {}", tempFile);
                } catch (IOException e) {
                    logger.warn("Failed to delete temporary file: {}", tempFile, e);
                }
            }
        }
    }

    /**
     * Helper method to get appropriate file extension based on content type
     */
    private String getExtensionFromContentType(String contentType, String originalFilename) {
        // Default extension
        String extension = ".dat";

        if (contentType == null) {
            // Try to extract from filename if content type is not available
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            return extension;
        }

        // Map common content types to extensions
        switch (contentType) {
            case "audio/webm":
            case "video/webm":
                return ".webm";
            case "audio/mp3":
            case "audio/mpeg":
                return ".mp3";
            case "audio/wav":
                return ".wav";
            case "audio/ogg":
                return ".ogg";
            case "image/jpeg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                return ".gif";
            case "application/pdf":
                return ".pdf";
            case "text/plain":
                return ".txt";
            default:
                // Try to extract from filename for unknown content types
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                return extension;
        }
    }
}
