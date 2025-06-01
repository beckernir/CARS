package com.cars.child_abuse_reporting_system.exceptions;

import lombok.Getter;

/**
 * Exception thrown when a case is not found
 */
@Getter
public class CaseNotFoundException extends RuntimeException {
    public CaseNotFoundException(String message) {
        super(message);
    }
}

