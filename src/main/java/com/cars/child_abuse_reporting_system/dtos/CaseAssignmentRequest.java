package com.cars.child_abuse_reporting_system.dtos;

import com.cars.child_abuse_reporting_system.enums.PriorityLevel;
import lombok.Data;

import java.util.Set;

@Data
public class CaseAssignmentRequest {
    private Long caseId;
    private String authorityRole; // Should match enum name
    private String authorityContact;
    private String assignmentNotes;
    private PriorityLevel priorityLevel;

    // Getters and setters...
}
