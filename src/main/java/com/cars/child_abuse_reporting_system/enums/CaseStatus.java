package com.cars.child_abuse_reporting_system.enums;

public enum CaseStatus {
    REPORTED,      // Initial state when a case is first reported
    ASSIGNED,      // Case has been assigned to authorities
    INVESTIGATING, // Investigation is in progress
    RESOLVED,      // Case has been resolved
    CLOSED,        // Case has been closed (could be due to false report)
}