package com.cars.child_abuse_reporting_system.controllers;

import com.cars.child_abuse_reporting_system.dtos.CaseAssignmentRequest;
import com.cars.child_abuse_reporting_system.dtos.CaseReportDTO;
import com.cars.child_abuse_reporting_system.dtos.StatusUpdateRequest;
import com.cars.child_abuse_reporting_system.entities.CaseReport;
import com.cars.child_abuse_reporting_system.exceptions.CaseNotFoundException;
import com.cars.child_abuse_reporting_system.exceptions.FileProcessingException;
import com.cars.child_abuse_reporting_system.exceptions.InvalidRequestException;
import com.cars.child_abuse_reporting_system.exceptions.UnauthorizedOperationException;
import com.cars.child_abuse_reporting_system.repositories.CaseReportRepository;
import com.cars.child_abuse_reporting_system.services.CaseAssignmentService;
import com.cars.child_abuse_reporting_system.services.CaseReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/case-reports")
@Tag(name = "Case Report Controller", description = "API endpoints for managing case reports")
public class CaseReportRestController {

    @Autowired
    private CaseReportService caseReportService;

    @Autowired
    private CaseReportRepository caseReportRepository;

    @Autowired
    private CaseAssignmentService caseAssignmentService;

    @PutMapping("/{caseId}/status")
    public ResponseEntity<CaseReport> updateCaseStatus(
            @PathVariable String caseId,
            @RequestBody StatusUpdateRequest request) {
        CaseReport updatedReport = caseReportService.updateReportStatus(caseId, request.getNewStatus());
        return ResponseEntity.ok(updatedReport);
    }

    @PostMapping("/assign")
    @ResponseBody
    public ResponseEntity<?> assignCaseToAuthority(@RequestBody CaseAssignmentRequest caseAssignment) {
        try {
            // Process the assignment
            caseAssignmentService.assignCaseToAuthority(caseAssignment);

            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "Case assigned successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to assign case: " + e.getMessage()
            ));
        }
    }
    /**
     * Get a case report by its ID
     *
     * @param caseId The case ID
     * @return ResponseEntity with the case report
     */
    @GetMapping("/{caseId}")
    @Operation(summary = "Get a case report by ID", description = "Retrieves a case report by its unique identifier")
    @ApiResponse(responseCode = "200", description = "Case report found")
    @ApiResponse(responseCode = "404", description = "Case report not found")
    public ResponseEntity<?> getReportById(@PathVariable Long caseId) {
        try {
            Optional<CaseReport> report = caseReportRepository.findById(caseId);
            return ResponseEntity.ok(report);
        } catch (CaseNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/case/{caseId}")

    public ResponseEntity<?> getReportByCaseId(@PathVariable String caseId) {
        try {
            Optional<CaseReport> report = caseReportRepository.findByCaseId(caseId);
            return ResponseEntity.ok(report);
        } catch (CaseNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}