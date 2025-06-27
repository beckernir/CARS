package com.cars.child_abuse_reporting_system.services;

import com.cars.child_abuse_reporting_system.dtos.CaseAssignmentRequest;
import com.cars.child_abuse_reporting_system.dtos.SendEmailDto;
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
    private final EmailService emailService;

    @Autowired
    public CaseAssignmentService(CaseAssignmentRepository assignmentRepository, CaseReportRepository caseRepository, EmailService emailService) {
        this.assignmentRepository = assignmentRepository;
        this.caseRepository = caseRepository;
        this.emailService = emailService;
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

//        sendCaseAssignmentEmail(caseEntity.getCaseId());

        return savedAssignment;
    }
//    private void sendCaseAssignmentEmail(String caseId) {
//        String htmlTemplate = String.format(
//                "<!DOCTYPE html>" +
//                        "<html lang=\"en\">" +
//                        "<head>" +
//                        "<meta charset=\"UTF-8\" />" +
//                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />" +
//                        "<title>Case Assignment Notification</title>" +
//                        "<link href=\"https://fonts.googleapis.com/css2?family=Lato:wght@400;700&display=swap\" rel=\"stylesheet\" />" +
//                        "</head>" +
//                        "<body style=\"font-family: 'Lato', sans-serif; background-color: #F2F8FC; padding: 20px;\">" +
//                        "<div style=\"max-width: 600px; margin: auto; background: #FFFFFF; border: 1px solid #D1E3F8; border-radius: 8px; padding: 20px;\">" +
//                        "<div style=\"text-align: center; margin-bottom: 20px;\">" +
//                        "<img src=\"https://www.auca.ac.rw/images/auca-logo.png\" alt=\"AUCA Logo\" style=\"width: 150px;\"/>" +
//                        "</div>" +
//                        "<h1 style=\"font-size: 20px; color: #003366;\">Hi %s,</h1>" +
//                        "<p>You have been <strong>assigned to investigate Case #%s</strong> regarding a child abuse report.</p>" +
//                        "<p>Please log in to the system to review the case details and begin your investigation without delay.</p>" +
//                        "<div style=\"text-align: center;\">" +
//                        "<a href=\"#\" style=\"display: inline-block; margin-top: 15px; padding: 10px 20px; background: #003366; color: #FFFFFF; text-decoration: none; border-radius: 5px;\">View Assigned Case</a>" +
//                        "</div>" +
//                        "<p style=\"margin-top: 30px; color: #666666; font-size: 14px;\">If you have any questions, reach out to the supervising officer or system administrator.</p>" +
//                        "<p style=\"color: #999999; font-size: 12px; text-align: center;\">&copy; 2025 AUCA Case Management System. All rights reserved.</p>" +
//                        "</div>" +
//                        "</body>" +
//                        "</html>",
//                 caseId
//        );
//
//        SendEmailDto emailDto = SendEmailDto.builder()
//                .to(email)
//                .subject("You’ve Been Assigned a New Case – Action Required")
//                .body(htmlTemplate)
//                .build();
//
//        emailService.sendEmail(emailDto);
//    }


    public List<CaseAssignment> getAssignmentsForCase(Long caseId) {
        return assignmentRepository.findByAssignedCaseId(caseId);
    }
    public List<CaseReport> getCasesAssignedToRole(Role role) {
        return assignmentRepository.findCasesByRole(role);
    }

}
