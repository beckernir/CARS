package com.cars.child_abuse_reporting_system.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyTrendDTO {
    private String month; // Format: YYYY-MM
    private Long totalCases;
}
