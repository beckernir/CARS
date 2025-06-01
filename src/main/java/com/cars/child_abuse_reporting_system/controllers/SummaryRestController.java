package com.cars.child_abuse_reporting_system.controllers;

import com.cars.child_abuse_reporting_system.dtos.SummaryDTO;
import com.cars.child_abuse_reporting_system.entities.Summary;
import com.cars.child_abuse_reporting_system.services.SummaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/summaries")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SummaryRestController {

    private final SummaryService summaryService;

    /**
     * Get all summaries for a specific case
     */
    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<Summary>> getSummariesByCase(@PathVariable Long caseId) {
        List<Summary> summaries = summaryService.findByCaseId(caseId);
        return ResponseEntity.ok(summaries);
    }

    /**
     * Get a specific summary by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Summary> getSummaryById(@PathVariable String id) {
        Optional<Summary> summary = summaryService.findById(id);
        return summary.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new summary for a selected case
     */
    @PostMapping
    public ResponseEntity<Summary> createSummary(@Valid @RequestBody SummaryDTO summaryDto) {
        try {
            Summary createdSummary = summaryService.createSummary(summaryDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSummary);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Create a new summary for a specific case (alternative endpoint with case ID in path)
     */
    @PostMapping("/case/{caseId}")
    public ResponseEntity<Summary> createSummaryForCase(
            @PathVariable Long caseId,
            @Valid @RequestBody SummaryDTO summaryDto) {
        try {
            // Ensure the DTO has the correct case ID from the path
            summaryDto.setCaseId(caseId);
            Summary createdSummary = summaryService.createSummary(summaryDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSummary);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update an existing summary
     */
    @PutMapping("/{id}")
    public ResponseEntity<Summary> updateSummary(
            @PathVariable String id,
            @Valid @RequestBody SummaryDTO summaryDto) {
        try {
            Summary updatedSummary = summaryService.updateSummary(id, summaryDto);
            return ResponseEntity.ok(updatedSummary);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a summary
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSummary(@PathVariable String id) {
        try {
            summaryService.deleteSummary(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}