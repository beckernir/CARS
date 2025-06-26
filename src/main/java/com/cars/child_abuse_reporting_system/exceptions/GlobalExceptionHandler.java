package com.cars.child_abuse_reporting_system.exceptions;

import com.cars.child_abuse_reporting_system.exceptions.FileValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileValidationException.class)
    public ResponseEntity<Object> handleFileValidationException(FileValidationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "File Validation Error");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.PAYLOAD_TOO_LARGE.value());
        body.put("error", "File Size Exceeded");
        body.put("message", "One or more files exceed the maximum allowed size");

        return new ResponseEntity<>(body, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Server Error");
        body.put("message", "An unexpected error occurred");

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
        errorResponse.put("error", "Authentication Required");
        errorResponse.put("message", "Please log in to access this resource.");
        errorResponse.put("details", "Your session may have expired or you need to sign in.");
        errorResponse.put("path", request.getDescription(false).replace("uri=", ""));
        errorResponse.put("suggestion", "Please return to the login page and sign in with your credentials.");

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    // For authenticated users without proper permissions
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.FORBIDDEN.value());
        errorResponse.put("error", "Access Denied");
        errorResponse.put("message", "You do not have permission to access this resource.");
        errorResponse.put("details", "Your account role does not allow access to this section of the system.");
        errorResponse.put("path", request.getDescription(false).replace("uri=", ""));
        errorResponse.put("suggestion", "Contact your system administrator if you believe you should have access to this resource.");

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

//    // Generic unauthorized access
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Map<String, Object>> handleGenericUnauthorized(
//            Exception ex, WebRequest request) {
//
//        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("access")) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("timestamp", LocalDateTime.now());
//            errorResponse.put("status", HttpStatus.FORBIDDEN.value());
//            errorResponse.put("error", "Unauthorized Access");
//            errorResponse.put("message", "Access to this resource is restricted.");
//            errorResponse.put("details", "Please verify your account permissions or contact support.");
//            errorResponse.put("path", request.getDescription(false).replace("uri=", ""));
//
//            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
//        }
//
//    }
}
