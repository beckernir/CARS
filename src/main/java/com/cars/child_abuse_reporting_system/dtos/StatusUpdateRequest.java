package com.cars.child_abuse_reporting_system.dtos;

import com.cars.child_abuse_reporting_system.enums.CaseStatus;
import lombok.Data;

@Data
public  class StatusUpdateRequest {
    private CaseStatus newStatus;
}