package com.cars.child_abuse_reporting_system.exceptions;

import lombok.Getter; /**
 * Exception thrown when file processing fails
 */
@Getter
public class FileProcessingException extends RuntimeException {
    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
