package com.cars.child_abuse_reporting_system.viewControllers;

import com.cars.child_abuse_reporting_system.dtos.CaseReportDTO;
import com.cars.child_abuse_reporting_system.dtos.InterviewDTO;
import com.cars.child_abuse_reporting_system.dtos.SummaryDTO;
import com.cars.child_abuse_reporting_system.entities.CaseReport;
import com.cars.child_abuse_reporting_system.entities.Summary;
import com.cars.child_abuse_reporting_system.enums.CaseStatus;
import com.cars.child_abuse_reporting_system.exceptions.CaseNotFoundException;
import com.cars.child_abuse_reporting_system.exceptions.FileProcessingException;
import com.cars.child_abuse_reporting_system.exceptions.InvalidRequestException;
import com.cars.child_abuse_reporting_system.exceptions.UnauthorizedOperationException;
import com.cars.child_abuse_reporting_system.services.CaseReportService;

import com.cars.child_abuse_reporting_system.services.InterviewService;
import com.cars.child_abuse_reporting_system.services.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * View controller for handling case report web pages and forms
 */
@Controller
@RequestMapping("/api/v1/cases")
public class CaseReportViewController {

    private CaseReportService caseReportService;

    private SummaryService summaryService;

    private InterviewService interviewService;

    @Autowired
    public CaseReportViewController(CaseReportService caseReportService, SummaryService summaryService, InterviewService interviewService) {
        this.caseReportService = caseReportService;
        this.summaryService = summaryService;
        this.interviewService = interviewService;
    }

    /**
     * Display form for submitting a new case report
     */
    @GetMapping("/new")
    public String showReportForm(Model model) {
        model.addAttribute("caseReportDTO", new CaseReportDTO());
        model.addAttribute("caseStatuses", CaseStatus.values());
        return "/forms/case-report";
    }

    @PostMapping("/submit")
    public String submitReport(@ModelAttribute CaseReportDTO cowDto, Model model,
                               RedirectAttributes redirectAttributes,
                               Authentication authentication) {

        // Handle both authenticated and anonymous users
        String userId;
        if (authentication != null && authentication.isAuthenticated()) {
            userId = authentication.getName(); // Get the username/ID for logged-in users
        } else {
            userId = "ANONYMOUS"; // Or generate a session-based ID, or use null
        }

        // Save the report using your service
        String savedReport = caseReportService.submitReport(cowDto, userId);

        // Add the case ID to the redirect attributes so it persists after redirect
        redirectAttributes.addFlashAttribute("caseId", savedReport);

        // Redirect to the confirmation page
        return "redirect:/api/v1/cases/confirm";
    }

    @GetMapping("/confirm")
    public String getConfirmationPage(Model model) {
        // For debugging purposes
        if (model.containsAttribute("caseId")) {
            System.out.println("Case ID found in model: " + model.getAttribute("caseId"));
        } else {
            System.out.println("No case ID in model");
        }

        return "confirmationAbuseReport";
    }
    /**
     * Display details of a specific case report
     */
    @GetMapping("/views/{caseId}")
    public String viewReport(@PathVariable String caseId, Model model, RedirectAttributes redirectAttributes) {
        try {
            CaseReport report = caseReportService.getReportByCaseId(caseId);
            model.addAttribute("report", report);
//            model.addAttribute("statusUpdateRequest", new CaseStatusUpdateRequest());
            model.addAttribute("caseStatuses", CaseStatus.values());
            return "cases/view";
        } catch (CaseNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cases/dashboard";
        }
    }

    /**
     * Display form for editing an existing case report
     */
    @GetMapping("/edit/{caseId}")
    public String showEditForm(
            @PathVariable String caseId,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            CaseReport report = caseReportService.getReportByCaseId(caseId);
//            CaseReportDTO reportDTO = convertToDTO(report);

            model.addAttribute("caseId", caseId);
            model.addAttribute("caseReportDTO", new CaseReportDTO());
            model.addAttribute("caseStatuses", CaseStatus.values());
            return "cases/edit-form";
        } catch (CaseNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cases/dashboard";
        }
    }

    /**
     * Process the update of an existing case report
     */
    @PostMapping("/edit/{caseId}")
    public String updateReport(
            @PathVariable String caseId,
            @Valid @ModelAttribute CaseReportDTO reportDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("caseId", caseId);
            model.addAttribute("caseStatuses", CaseStatus.values());
            return "cases/edit-form";
        }

        try {
            caseReportService.updateReport(caseId, reportDTO);
            redirectAttributes.addFlashAttribute("success", "Case report updated successfully");
            return "redirect:/cases/view/" + caseId;
        } catch (CaseNotFoundException | InvalidRequestException |
                 UnauthorizedOperationException | FileProcessingException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("caseId", caseId);
            model.addAttribute("caseStatuses", CaseStatus.values());
            return "cases/edit-form";
        }
    }
    /**
     * Process the deletion of a case report
     */
    @PostMapping("/delete/{caseId}")
    public String deleteReport(
            @PathVariable String caseId,
            RedirectAttributes redirectAttributes) {

        try {
            caseReportService.deleteReport(caseId);
            redirectAttributes.addFlashAttribute("success",
                    "Case report with ID " + caseId + " deleted successfully");
        } catch (CaseNotFoundException | UnauthorizedOperationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/cases/dashboard";
    }

    /**
     * Display search form and results
     */
    @GetMapping("/search")
    public String searchReports(
            @RequestParam(required = false) String childName,
            @RequestParam(required = false) String reporterName,
            @RequestParam(required = false) String status,
            Model model) {

        model.addAttribute("childName", childName);
        model.addAttribute("reporterName", reporterName);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("caseStatuses", CaseStatus.values());

        List<CaseReport> results = null;

        if (childName != null && !childName.trim().isEmpty()) {
            results = caseReportService.searchByChildName(childName);
        } else if (reporterName != null && !reporterName.trim().isEmpty()) {
            results = caseReportService.searchByReporterName(reporterName);
        } else if (status != null && !status.trim().isEmpty()) {
            try {
                CaseStatus caseStatus = CaseStatus.valueOf(status.toUpperCase());
                results = caseReportService.searchByStatus(caseStatus);
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", "Invalid status: " + status);
            }
        }

        if (results != null) {
            model.addAttribute("results", results);
        }

        return "cases/search";
    }
    /**
     * Display the list of cows.
     */
    @GetMapping("/allCases")
    public String getAllCows(Model model) {
        List<CaseReport> cows = caseReportService.getAll();
        model.addAttribute("caseReports", cows);
        return "/tables/admin-case-report-list"; // Thymeleaf template "list.html"
    }
    @PostMapping("/track")
    public String lookupCase(@RequestParam("caseId") String caseId, RedirectAttributes redirectAttributes) {
        try {
            // Trim whitespace and validate input
            String cleanCaseId = caseId.trim();

            if (cleanCaseId.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please enter a Case ID.");
                return "redirect:/api/v1/cases/confirm";
            }

            // Look up the case report by case ID
            Optional<CaseReport> foundCase = Optional.ofNullable(caseReportService.getReportByCaseId(cleanCaseId));

            if (foundCase.isPresent()) {
                // Redirect to view page with internal DB ID
                return "redirect:/api/v1/cases/view/" + foundCase.get().getId();
            } else {
                // Case not found - redirect back with error
                redirectAttributes.addFlashAttribute("error", "Case ID '" + cleanCaseId + "' not found. Please check the Case ID and try again.");
                redirectAttributes.addFlashAttribute("caseId", cleanCaseId); // Preserve the entered case ID
                return "redirect:/api/v1/cases/confirm";
            }
        } catch (Exception e) {
            // Log the error (use your preferred logging framework)
            // logger.error("Error looking up case with ID: " + caseId, e);

            redirectAttributes.addFlashAttribute("error", "An error occurred while looking up the case. Please try again.");
            return "redirect:/api/v1/cases/confirm";
        }
    }

    @GetMapping("/view/{id}")
    public String getViewOne(@PathVariable Long id, Model model) {
        // Get the case report
        Optional<CaseReport> caseReportOpt = Optional.ofNullable(caseReportService.getCaseReportById(id));

        if (caseReportOpt.isEmpty()) {
            // Handle case not found - redirect to dashboard or show error
            return "redirect:/";
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

        return "/public/anynomous-view-one";
    }
}

