package com.cars.child_abuse_reporting_system.viewControllers;

import com.cars.child_abuse_reporting_system.dtos.*;
import com.cars.child_abuse_reporting_system.entities.CaseReport;
import com.cars.child_abuse_reporting_system.entities.Interview;
import com.cars.child_abuse_reporting_system.entities.Summary;
import com.cars.child_abuse_reporting_system.entities.User;
import com.cars.child_abuse_reporting_system.enums.AbuseType;
import com.cars.child_abuse_reporting_system.enums.CaseStatus;
import com.cars.child_abuse_reporting_system.enums.Gender;
import com.cars.child_abuse_reporting_system.repositories.CaseReportRepository;
import com.cars.child_abuse_reporting_system.services.CaseReportService;
import com.cars.child_abuse_reporting_system.services.InterviewService;
import com.cars.child_abuse_reporting_system.services.SummaryService;
import com.cars.child_abuse_reporting_system.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/v1/admin")
public class AdminViewController {

    private final CaseReportService caseReportService;

    private final CaseReportRepository caseReportRepository;

    private final SummaryService summaryService;

    private final InterviewService interviewService;

    private final UserService userService;

    @Autowired
    public AdminViewController(CaseReportService caseReportService,
                               CaseReportRepository caseReportRepository,
                               SummaryService summaryService,
                               InterviewService interviewService,
                               UserService userService) {
        this.caseReportService = caseReportService;
        this.caseReportRepository = caseReportRepository;
        this.summaryService = summaryService;
        this.interviewService = interviewService;
        this.userService = userService;
    }
    /**
     * Main dashboard page
     */
    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        try {
            // Get all analytics data
            DashboardAnalyticsDTO analytics = caseReportService.getDashboardAnalytics();
            CaseStatisticsDTO statistics = caseReportService.getCaseStatistics();
            List<AgeGroupAnalysisDTO> ageAnalysis = caseReportService.getAgeGroupAnalysis();
            List<RegionAnalysisDTO> regionAnalysis = caseReportService.getRegionAnalysis();
            List<AbuseTypeAnalysisDTO> abuseTypeAnalysis = caseReportService.getAbuseTypeAnalysis();
            List<MonthlyTrendDTO> monthlyTrends = caseReportService.getMonthlyTrends();
            List<CaseReport> recentCases = caseReportService.getActiveCases();

            // Limit recent cases to last 10 for display
            if (recentCases.size() > 10) {
                recentCases = recentCases.subList(0, 10);
            }

            // Add all data to model
            model.addAttribute("analytics", analytics);
            model.addAttribute("statistics", statistics);
            model.addAttribute("ageAnalysis", ageAnalysis);
            model.addAttribute("regionAnalysis", regionAnalysis);
            model.addAttribute("abuseTypeAnalysis", abuseTypeAnalysis);
            model.addAttribute("monthlyTrends", monthlyTrends);
            model.addAttribute("recentCases", recentCases);
            model.addAttribute("caseStatuses", CaseStatus.values());


            return "/admin/admin-dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load dashboard data: " + e.getMessage());
            return "error";
        }
    }



    /**
     * Display the list of cows.
     */
    @GetMapping("/users/allUsers")
    public String getAllUsers(Model model) {
        List<User> users = userService.getAllUsersNoPage();
        model.addAttribute("users", users);
        return "/admin/users-list"; // Thymeleaf template "list.html"
    }
    /**
     * Delete a cow by ID.
     */
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return "redirect:/api/v1/admin/users/allUsers";
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

        return "/admin/view-one";
    }

    /**
     * Display the list of cows.
     */
    @GetMapping("/allCases")
    public String getAllCases(Model model) {
        List<CaseReport> cows = caseReportService.getAll();
        model.addAttribute("caseReports", cows);
        return "/admin/admin-case-report-list";
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

        return "/admin/new-report";
    }

    // Handle form submission
    /**
     * Process the cow creation form.
     */
    @PostMapping("/submit")
    public String createCow(@ModelAttribute CaseReportDTO cowDto, Authentication authentication) {
        String userId = authentication.getName(); // This gets the username/ID

        caseReportService.submitReport(cowDto,userId);
        return "redirect:/api/v1/admin/allCases";
    }
    /**
     * Delete a case report by ID.
     */
    @GetMapping("/delete/{id}")
    public String deleteCaseReport(@PathVariable Long id) {
        caseReportRepository.deleteById(id);
        return "redirect:/api/v1/admin/allCases";
    }
    /**
     * Get list of active cases
     */
    @GetMapping("/active")
    public String getAllActiveCases(Model model) {
        List<CaseReport> cows = caseReportService.getActiveCases();
        model.addAttribute("activeCases", cows);
        return "/admin/active-cases";
    }

    /**
     * Get list of closed cases
     */
    @GetMapping("/closed")
    public String getAllClosedCases(Model model) {
        List<CaseReport> cows = caseReportService.getClosedCases();
        model.addAttribute("closedCases", cows);
        return "/admin/closed-cases";
    }


}
