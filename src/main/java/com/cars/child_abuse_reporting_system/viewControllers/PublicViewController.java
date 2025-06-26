package com.cars.child_abuse_reporting_system.viewControllers;

import com.cars.child_abuse_reporting_system.config.CustomUserDetailsService;
import com.cars.child_abuse_reporting_system.dtos.CaseReportDTO;
import com.cars.child_abuse_reporting_system.dtos.InterviewDTO;
import com.cars.child_abuse_reporting_system.dtos.SummaryDTO;
import com.cars.child_abuse_reporting_system.entities.CaseReport;
import com.cars.child_abuse_reporting_system.entities.Summary;
import com.cars.child_abuse_reporting_system.entities.User;
import com.cars.child_abuse_reporting_system.enums.AbuseType;
import com.cars.child_abuse_reporting_system.enums.Gender;
import com.cars.child_abuse_reporting_system.enums.Role;
import com.cars.child_abuse_reporting_system.repositories.CaseReportRepository;
import com.cars.child_abuse_reporting_system.services.CaseReportService;
import com.cars.child_abuse_reporting_system.services.InterviewService;
import com.cars.child_abuse_reporting_system.services.SummaryService;
import com.cars.child_abuse_reporting_system.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicViewController {
    private CaseReportService caseReportService;

    private UserService userService;

    private CaseReportRepository caseReportRepository;

    private SummaryService summaryService;

    private InterviewService interviewService;

    @Autowired
    public PublicViewController(CaseReportService caseReportService,
                                UserService userService,
                                CaseReportRepository caseReportRepository,
                                SummaryService summaryService,
                                InterviewService interviewService) {
        this.caseReportService = caseReportService;
        this.userService = userService;
        this.caseReportRepository = caseReportRepository;
        this.summaryService = summaryService;
        this.interviewService = interviewService;
    }

    //    private Long getUserIdFromAuthentication(Authentication authentication) {
//        // This depends on your authentication setup
//        // Example implementations:
//
//        // If using custom UserDetails:
//         UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//         return ((CustomUserDetailsService) userDetails).();
//
////         If using JWT with user ID in subject:
////         return Long.parseLong(authentication.getName());
//
////         If using Spring Security with user ID as principal:
//         return (Long) authentication.getPrincipal();
//
//        // Placeholder - replace with your actual implementation
//        return Long.parseLong(authentication.getName());
//    }
//
private String addUserDataToModel(Authentication authentication) {
    String username = "";
    if (authentication != null && authentication.isAuthenticated()) {
         username = authentication.getName();

        // Fetch user from database
        User user = userService.findByUsername(username);
    }
    return username;
}
    /**
     * Get all cases created by the logged-in user
     */
    @GetMapping
    public ResponseEntity<List<CaseReport>> getUserCases(Authentication authentication) {
        String userId = addUserDataToModel(authentication);
        List<CaseReport> userCases = caseReportService.getCasesByUserId(userId);
        return ResponseEntity.ok(userCases);
    }
    /**
     * Display the list of cases.
     */
    @GetMapping("/allCases")
    public String getAllCases(Model model,Authentication authentication) {
        String userId = authentication.getName(); // This gets the username/ID

        List<CaseReport> userCases = caseReportService.getCasesByUserId(userId);
        model.addAttribute("currentUser", userId);
        model.addAttribute("caseReports", userCases);
        return "/public/my-cases";
    }

    /**
     * Display form for submitting a new case report
     */
    @GetMapping("/new")
    public String showReportForm(Model model) {
        model.addAttribute("caseReportDTO", new CaseReportDTO());
        List<Gender> gender = Arrays.stream(Gender.values()).toList();
        model.addAttribute("gender", gender);
        List<AbuseType> abuseTypes = Arrays.stream(AbuseType.values()).toList();
        model.addAttribute("abuseTypes", abuseTypes);

        return "/public/new-report";
    }

    // Handle form submission
    /**
     * Process the cow creation form.
     */
    @PostMapping("/submit")
    public String createCow(@ModelAttribute CaseReportDTO cowDto, Authentication authentication) {
        String userId = authentication.getName(); // This gets the username/ID
        caseReportService.submitReport(cowDto,userId);
        return "redirect:/api/v1/public/allCases";
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
            return "redirect:/api/v1/admin/dashboard";
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

        return "/public/view-one";
    }
}
