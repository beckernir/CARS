package com.cars.child_abuse_reporting_system.services;

import com.cars.child_abuse_reporting_system.dtos.SummaryDTO;
import com.cars.child_abuse_reporting_system.entities.CaseReport;
import com.cars.child_abuse_reporting_system.entities.Summary;
import com.cars.child_abuse_reporting_system.enums.CaseStatus;
import com.cars.child_abuse_reporting_system.repositories.CaseReportRepository;
import com.cars.child_abuse_reporting_system.repositories.SummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final SummaryRepository summaryRepository;
    private final CaseReportRepository caseReportRepository;

    @Transactional(readOnly = true)
    public List<Summary> findByCaseId(Long caseId) {
        return summaryRepository.findByRelatedCaseId(caseId);
    }

    @Transactional(readOnly = true)
    public Optional<Summary> findById(String id) {
        return summaryRepository.findById(id);
    }

    @Transactional
    public Summary createSummary( SummaryDTO summaryDto) {

        CaseReport caseReport = caseReportRepository.findById(summaryDto.getCaseId())
                .orElseThrow(() -> new IllegalArgumentException("Case not found with ID: " + summaryDto.getCaseId()));

        Summary summary = Summary.builder()
                .relatedCase(caseReport)
                .noteType(summaryDto.getNoteType())
                .description(summaryDto.getDescription())
                .incidentDetails(summaryDto.getIncidentDetails())
                .initialAssessment(summaryDto.getInitialAssessment())
                .currentUpdates(summaryDto.getCurrentUpdates())
                .build();
        
        if (!CaseStatus.INVESTIGATING.name().equalsIgnoreCase(caseReport.getStatus().name())) {
            caseReport.setStatus(CaseStatus.valueOf(CaseStatus.INVESTIGATING.name()));
            caseReportRepository.save(caseReport);
        }

        return summaryRepository.save(summary);
    }

    @Transactional
    public Summary updateSummary(String id, SummaryDTO summary) {

        Summary existingSummary = summaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Summary not found with ID: " + id));

        existingSummary.setNoteType(summary.getNoteType());
        existingSummary.setDescription(summary.getDescription());
        existingSummary.setIncidentDetails(summary.getIncidentDetails());
        existingSummary.setInitialAssessment(summary.getInitialAssessment());
        existingSummary.setCurrentUpdates(summary.getCurrentUpdates());

        return summaryRepository.save(existingSummary);
    }

    @Transactional
    public void deleteSummary(String id) {
        summaryRepository.deleteById(id);
    }
}