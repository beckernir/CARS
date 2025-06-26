package com.cars.child_abuse_reporting_system.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAnalyticsDTO {
    private CaseStatisticsDTO caseStatistics;
    private List<AgeGroupAnalysisDTO> ageGroupAnalysis;
    private List<RegionAnalysisDTO> regionAnalysis;
    private List<AbuseTypeAnalysisDTO> abuseTypeAnalysis;
    private List<MonthlyTrendDTO> monthlyTrends;
    private LocalDateTime lastUpdated;
}