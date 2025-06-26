package com.cars.child_abuse_reporting_system.viewControllers;

import com.cars.child_abuse_reporting_system.dtos.CaseAssignmentRequest;
import com.cars.child_abuse_reporting_system.entities.CaseAssignment;
import com.cars.child_abuse_reporting_system.entities.CaseReport;
import com.cars.child_abuse_reporting_system.enums.PriorityLevel;
import com.cars.child_abuse_reporting_system.enums.Role;
import com.cars.child_abuse_reporting_system.repositories.CaseReportRepository;
import com.cars.child_abuse_reporting_system.services.CaseAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CaseAssignmentViewController {

    private final CaseReportRepository caseRepository;
    private final CaseAssignmentService caseAssignmentService;


    @GetMapping("/api/case-assignments/assign")
    public String showAssignCasePage(@RequestParam("caseId") Long caseId, Model model) {
        CaseReport caseReport = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found with id: " + caseId));

        model.addAttribute("caseReport", caseReport);
        model.addAttribute("roles", Role.values());
        model.addAttribute("priorityLevels", PriorityLevel.values());
        model.addAttribute("assignmentRequest", new CaseAssignment());

        return "/tables/admin-case-report-listi"; // Thymeleaf or JSP page name
    }
    @PostMapping("/api/case-assignments/assign")
    public String assignCaseToAuthority(@ModelAttribute("assignmentRequest") CaseAssignmentRequest request, RedirectAttributes redirectAttributes) {
        try {
            CaseAssignment savedAssignment = caseAssignmentService.assignCaseToAuthority(request);
            redirectAttributes.addFlashAttribute("successMessage", "Case assigned successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error assigning case: " + e.getMessage());
            return "redirect:/assign-case?caseId=" + request.getCaseId();
        }
        // Redirect to a suitable page after assignment, e.g. case details page
        return "redirect:/cases/" + request.getCaseId();
    }

}
