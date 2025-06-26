package com.cars.child_abuse_reporting_system.exceptions;

import lombok.Getter; /**
 * Exception thrown when an unauthorized operation is attempted
 */
@Getter
public class UnauthorizedOperationException extends RuntimeException {
    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
