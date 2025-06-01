package com.cars.child_abuse_reporting_system.dtos;

import java.time.LocalDateTime;

public class ApiErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String details;
    private String path;
    private String suggestion;

    // Constructors, getters, and setters
    public ApiErrorResponse(int status, String error, String message, String details, String path, String suggestion) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
        this.path = path;
        this.suggestion = suggestion;
    }

    // Getters and setters...
    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getDetails() { return details; }
    public String getPath() { return path; }
    public String getSuggestion() { return suggestion; }
}

