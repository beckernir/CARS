package com.cars.child_abuse_reporting_system.viewControllers;

import com.cars.child_abuse_reporting_system.dtos.InterviewDTO;
import com.cars.child_abuse_reporting_system.dtos.SummaryDTO;
import com.cars.child_abuse_reporting_system.entities.CaseReport;
import com.cars.child_abuse_reporting_system.entities.Summary;
import com.cars.child_abuse_reporting_system.entities.User;
import com.cars.child_abuse_reporting_system.enums.Role;
import com.cars.child_abuse_reporting_system.repositories.CaseReportRepository;
import com.cars.child_abuse_reporting_system.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/v1/authority")
public class AuthorityController {
    private CaseReportService caseReportService;

    private UserService userService;

    private CaseReportRepository caseReportRepository;

    private SummaryService summaryService;

    private InterviewService interviewService;
    private CaseAssignmentService caseAssignmentService;

    @Autowired
    public AuthorityController(CaseReportService caseReportService,
                               UserService userService,
                               CaseReportRepository caseReportRepository,
                               SummaryService summaryService,
                               InterviewService interviewService,
                               CaseAssignmentService caseAssignmentService) {
        this.caseReportService = caseReportService;
        this.userService = userService;
        this.caseReportRepository = caseReportRepository;
        this.summaryService = summaryService;
        this.interviewService = interviewService;
        this.caseAssignmentService = caseAssignmentService;
    }

    /**
     * Display the list of cases.
     */
    @GetMapping("/allCases")
    public String getAllCasesAlternative(Model model, Authentication authentication) {
        String username = authentication.getName();

        // Get user details including role
        User currentUser = userService.findByUsername(username);
        Role userRole = currentUser.getRole();

        // Get cases assigned to this role
        List<CaseReport> userCases = caseAssignmentService.getCasesAssignedToRole(userRole);

        model.addAttribute("caseReports", userCases);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentUserRole", userRole);

        return "/authority/my-cases";
    }
    /**
     * Display a specific case with all its details and summaries
     */
    @GetMapping("/view/{id}")
    public String getViewOne(@PathVariable Long id, Model model) {
        // Get the case report
        Optional<CaseReport> caseReportOpt = caseReportRepository.findById(id);

        if (caseReportOpt.isEmpty()) {
            // Handle case not found - redirect to dashboard or show error
            return "redirect:/api/v1/authority/dashboard";
        }

        CaseReport caseReport = caseReportOpt.get();

        // Get summaries for this case
        List<Summary> summaries = summaryService.findByCaseId(id);

        // Create a new SummaryDTO for the modal form
        InterviewDTO interviewDTO = new InterviewDTO();
        interviewDTO.setCaseId(id); // Pre-populate with current case ID

        // Get summaries for this case
        List<InterviewDTO> interviews = interviewService.getInterviewsByCaseId(id);

        // Create a new SummaryDTO for the modal form
        SummaryDTO summaryDto = new SummaryDTO();
        summaryDto.setCaseId(id); // Pre-populate with current case ID

        // Add attributes to the model
        model.addAttribute("caseReport", caseReport);
        model.addAttribute("caseId", id);

        model.addAttribute("summaries", summaries);
        model.addAttribute("summaryDto", summaryDto);

        model.addAttribute("interviews", interviews);
        model.addAttribute("interviewDto", interviewDTO);

        return "/authority/view-one";
    }
}
