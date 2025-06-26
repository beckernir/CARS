package com.cars.child_abuse_reporting_system.services;

import com.cars.child_abuse_reporting_system.dtos.CaseAssignmentRequest;
import com.cars.child_abuse_reporting_system.entities.CaseAssignment;
import com.cars.child_abuse_reporting_system.entities.CaseReport;
import com.cars.child_abuse_reporting_system.enums.CaseStatus;
import com.cars.child_abuse_reporting_system.enums.Role;
import com.cars.child_abuse_reporting_system.repositories.CaseAssignmentRepository;
import com.cars.child_abuse_reporting_system.repositories.CaseReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CaseAssignmentService {

    private final CaseAssignmentRepository assignmentRepository;
    private final CaseReportRepository caseRepository;

    @Autowired
    public CaseAssignmentService(CaseAssignmentRepository assignmentRepository, CaseReportRepository caseRepository) {
        this.assignmentRepository = assignmentRepository;
        this.caseRepository = caseRepository;
    }
    @Transactional(readOnly = true)
    public List<CaseAssignment> findByCaseId(Long caseId) {
        return assignmentRepository.findByAssignedCaseId(caseId);
    }

    @Transactional
    public CaseAssignment assignCaseToAuthority(CaseAssignmentRequest request) {
        CaseReport caseEntity = caseRepository.findById(request.getCaseId())
                .orElseThrow(() -> new RuntimeException("Case not found with id: " + request.getCaseId()));

        Role authorityRole;
        try {
            authorityRole = Role.valueOf(request.getAuthorityRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid authority role: " + request.getAuthorityRole());
        }

//        // Create a Set with single role
//        Set<Role> roles = new HashSet<>();
//        roles.add(authorityRole);

        CaseAssignment assignment = CaseAssignment.builder()
                .assignedCase(caseEntity)
                .authorityRole(authorityRole)
                .assignmentNotes(request.getAssignmentNotes())
                .priorityLevel(request.getPriorityLevel())
                .assignedAt(LocalDateTime.now())
                .build();

        CaseAssignment savedAssignment = assignmentRepository.save(assignment);

        if (!CaseStatus.ASSIGNED.name().equalsIgnoreCase(caseEntity.getStatus().name())) {
            caseEntity.setStatus(CaseStatus.valueOf(CaseStatus.ASSIGNED.name()));
            caseRepository.save(caseEntity);
        }


        return savedAssignment;
    }


    public List<CaseAssignment> getAssignmentsForCase(Long caseId) {
        return assignmentRepository.findByAssignedCaseId(caseId);
    }
    public List<CaseReport> getCasesAssignedToRole(Role role) {
        return assignmentRepository.findCasesByRole(role);
    }

}
