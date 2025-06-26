package com.cars.child_abuse_reporting_system.exceptions;

import lombok.Getter; /**
 * Exception thrown when invalid data is provided in a request
 */
@Getter
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
