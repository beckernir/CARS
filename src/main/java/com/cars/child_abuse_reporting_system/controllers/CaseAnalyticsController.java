package com.cars.child_abuse_reporting_system.controllers;

import com.cars.child_abuse_reporting_system.dtos.*;
import com.cars.child_abuse_reporting_system.entities.CaseReport;
import com.cars.child_abuse_reporting_system.enums.CaseStatus;
import com.cars.child_abuse_reporting_system.exceptions.CaseNotFoundException;
import com.cars.child_abuse_reporting_system.services.CaseReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/analytics")
@CrossOrigin(origins = "*")
public class CaseAnalyticsController {

    @Autowired
    private CaseReportService caseReportService;

    /**
     * Get comprehensive dashboard analytics
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardAnalyticsDTO> getDashboardAnalytics() {
        DashboardAnalyticsDTO analytics = caseReportService.getDashboardAnalytics();
        return ResponseEntity.ok(analytics);
    }

    /**
     * Get basic case statistics (total, active, closed)
     */
    @GetMapping("/statistics")
    public ResponseEntity<CaseStatisticsDTO> getCaseStatistics() {
        CaseStatisticsDTO statistics = caseReportService.getCaseStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get list of active cases
     */
    @GetMapping("/cases/active")
    public ResponseEntity<List<CaseReport>> getActiveCases() {
        List<CaseReport> activeCases = caseReportService.getActiveCases();
        return ResponseEntity.ok(activeCases);
    }

    /**
     * Get list of closed cases
     */
    @GetMapping("/cases/closed")
    public ResponseEntity<List<CaseReport>> getClosedCases() {
        List<CaseReport> closedCases = caseReportService.getClosedCases();
        return ResponseEntity.ok(closedCases);
    }

    /**
     * Get age group analysis
     */
    @GetMapping("/age-groups")
    public ResponseEntity<List<AgeGroupAnalysisDTO>> getAgeGroupAnalysis() {
        List<AgeGroupAnalysisDTO> ageAnalysis = caseReportService.getAgeGroupAnalysis();
        return ResponseEntity.ok(ageAnalysis);
    }

    /**
     * Get regional analysis
     */
    @GetMapping("/regions")
    public ResponseEntity<List<RegionAnalysisDTO>> getRegionAnalysis() {
        List<RegionAnalysisDTO> regionAnalysis = caseReportService.getRegionAnalysis();
        return ResponseEntity.ok(regionAnalysis);
    }

    /**
     * Get abuse type analysis
     */
    @GetMapping("/abuse-types")
    public ResponseEntity<List<AbuseTypeAnalysisDTO>> getAbuseTypeAnalysis() {
        List<AbuseTypeAnalysisDTO> abuseTypeAnalysis = caseReportService.getAbuseTypeAnalysis();
        return ResponseEntity.ok(abuseTypeAnalysis);
    }

    /**
     * Get monthly trends for the last 12 months
     */
    @GetMapping("/trends/monthly")
    public ResponseEntity<List<MonthlyTrendDTO>> getMonthlyTrends() {
        List<MonthlyTrendDTO> trends = caseReportService.getMonthlyTrends();
        return ResponseEntity.ok(trends);
    }

    /**
     * Update single case status
     */
    @PutMapping("/cases/{caseId}/status")
    public ResponseEntity<CaseReport> updateCaseStatus(
            @PathVariable String caseId,
            @RequestParam CaseStatus status) {
        try {
            CaseReport updatedCase = caseReportService.updateReportStatus(caseId, status);
            return ResponseEntity.ok(updatedCase);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get cases by specific status
     */
    @GetMapping("/cases/status/{status}")
    public ResponseEntity<List<CaseReport>> getCasesByStatus(@PathVariable CaseStatus status) {
        List<CaseReport> cases = caseReportService.searchByStatus(status);
        return ResponseEntity.ok(cases);
    }
//
//    /**
//     * Get case counts by status
//     */
//    @GetMapping("/status-summary")
//    public ResponseEntity<StatusSummaryDTO> getStatusSummary() {
//        CaseStatisticsDTO stats = caseReportService.getCaseStatistics();
//
//        // You might want to get more detailed status breakdown
//        StatusSummaryDTO summary = StatusSummaryDTO.builder()
//                .totalCases(stats.getTotalCases())
//                .activeCases(stats.getActiveCases())
//                .closedCases(stats.getClosedCases())
//                .reportedCases(caseReportService.searchByStatus(CaseStatus.REPORTED).size())
////                .underInvestigationCases(caseReportService.searchByStatus(CaseStatus.UNDER_INVESTIGATION).size())
////                .inProgressCases(caseReportService.searchByStatus(CaseStatus.IN_PROGRESS).size())
//                .resolvedCases(caseReportService.searchByStatus(CaseStatus.RESOLVED).size())
////                .di
//    }
}