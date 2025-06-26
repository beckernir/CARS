package com.cars.child_abuse_reporting_system.exceptions;

import lombok.Getter; /**
 * Exception thrown when there's a conflict with existing data
 */
@Getter
public class DataConflictException extends RuntimeException {
    public DataConflictException(String message) {
        super(message);
    }
}
