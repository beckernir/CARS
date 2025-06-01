package com.cars.child_abuse_reporting_system.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseStatisticsDTO {
    private long totalCases;
    private long activeCases;
    private long closedCases;

    public double getActivePercentage() {
        return totalCases > 0 ? Math.round((activeCases * 100.0 / totalCases) * 100.0) / 100.0 : 0.0;
    }

    public double getClosedPercentage() {
        return totalCases > 0 ? Math.round((closedCases * 100.0 / totalCases) * 100.0) / 100.0 : 0.0;
    }
}





