package com.cars.child_abuse_reporting_system.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusSummaryDTO {
    private long totalCases;
    private long activeCases;
    private long closedCases;
    private long reportedCases;
    private long underInvestigationCases;
    private long inProgressCases;
    private long resolvedCases;
    private long dismissedCases;

    // Calculate percentages
    public double getActivePercentage() {
        return totalCases > 0 ? Math.round((activeCases * 100.0 / totalCases) * 100.0) / 100.0 : 0.0;
    }

    public double getClosedPercentage() {
        return totalCases > 0 ? Math.round((closedCases * 100.0 / totalCases) * 100.0) / 100.0 : 0.0;
    }

    public double getReportedPercentage() {
        return totalCases > 0 ? Math.round((reportedCases * 100.0 / totalCases) * 100.0) / 100.0 : 0.0;
    }

    public double getUnderInvestigationPercentage() {
        return totalCases > 0 ? Math.round((underInvestigationCases * 100.0 / totalCases) * 100.0) / 100.0 : 0.0;
    }

    public double getInProgressPercentage() {
        return totalCases > 0 ? Math.round((inProgressCases * 100.0 / totalCases) * 100.0) / 100.0 : 0.0;
    }

    public double getResolvedPercentage() {
        return totalCases > 0 ? Math.round((resolvedCases * 100.0 / totalCases) * 100.0) / 100.0 : 0.0;
    }

    public double getDismissedPercentage() {
        return totalCases > 0 ? Math.round((dismissedCases * 100.0 / totalCases) * 100.0) / 100.0 : 0.0;
    }
}