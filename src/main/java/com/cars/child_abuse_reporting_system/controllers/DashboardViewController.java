package com.cars.child_abuse_reporting_system.controllers;

import com.cars.child_abuse_reporting_system.dtos.*;
import com.cars.child_abuse_reporting_system.entities.CaseReport;
import com.cars.child_abuse_reporting_system.enums.CaseStatus;
import com.cars.child_abuse_reporting_system.services.CaseReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class DashboardViewController {

    @Autowired
    private CaseReportService caseReportService;

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
     * Cases management page
     */
    @GetMapping("/cases")
    public String getCasesPage(
            @RequestParam(required = false) CaseStatus status,
            Model model) {
        try {
            List<CaseReport> cases;
            if (status != null) {
                cases = caseReportService.searchByStatus(status);
                model.addAttribute("selectedStatus", status);
            } else {
                cases = caseReportService.getActiveCases();
            }

            model.addAttribute("cases", cases);
            model.addAttribute("caseStatuses", CaseStatus.values());
            model.addAttribute("statistics", caseReportService.getCaseStatistics());

            return "/admin/admin-dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load cases: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Analytics page with detailed charts
     */
    @GetMapping("/analytics")
    public String getAnalyticsPage(Model model) {
        try {
            model.addAttribute("ageAnalysis", caseReportService.getAgeGroupAnalysis());
            model.addAttribute("regionAnalysis", caseReportService.getRegionAnalysis());
            model.addAttribute("abuseTypeAnalysis", caseReportService.getAbuseTypeAnalysis());
            model.addAttribute("monthlyTrends", caseReportService.getMonthlyTrends());
            model.addAttribute("statistics", caseReportService.getCaseStatistics());

            return "/admin/admin-dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load analytics: " + e.getMessage());
            return "error";
        }
    }

    /**
     * AJAX endpoint for updating case status
     */
    @PostMapping("/cases/{caseId}/status")
    @ResponseBody
    public ResponseEntity<String> updateCaseStatus(
            @PathVariable String caseId,
            @RequestParam CaseStatus status) {
        try {
            caseReportService.updateReportStatus(caseId, status);
            return ResponseEntity.ok("Status updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update status: " + e.getMessage());
        }
    }
}