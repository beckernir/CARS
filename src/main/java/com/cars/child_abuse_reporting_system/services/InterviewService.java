package com.cars.child_abuse_reporting_system.services;

import com.cars.child_abuse_reporting_system.dtos.InterviewDTO;
import com.cars.child_abuse_reporting_system.entities.CaseReport;
import com.cars.child_abuse_reporting_system.entities.Interview;
import com.cars.child_abuse_reporting_system.enums.CaseStatus;
import com.cars.child_abuse_reporting_system.repositories.CaseReportRepository;
import com.cars.child_abuse_reporting_system.repositories.InterviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final CaseReportRepository caseReportRepository;

    public List<Interview> getAllInterviews() {
        log.info("Fetching all interviews");
        return interviewRepository.findAll();
    }

    public Optional<Interview> getInterviewById(Long id) {
        log.info("Fetching interview with id: {}", id);
        return interviewRepository.findById(id);
    }

    public List<InterviewDTO> getInterviewsByCaseId(Long caseId) {
        log.info("Fetching interviews for case id: {}", caseId);
        return interviewRepository.findByCaseReportIdOrderByInterviewDateDesc(caseId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public InterviewDTO createInterview(InterviewDTO interviewDTO) {
        log.info("Creating new interview for case id: {}", interviewDTO.getCaseId());

        CaseReport caseReport = caseReportRepository.findById(interviewDTO.getCaseId())
                .orElseThrow(() -> new RuntimeException("Case report not found with id: " + interviewDTO.getCaseId()));

        Interview interview = Interview.builder()
                .caseReport(caseReport)
                .intervieweeName(interviewDTO.getIntervieweeName())
                .interviewerName(interviewDTO.getInterviewerName())
                .interviewDate(interviewDTO.getInterviewDate())
                .location(interviewDTO.getLocation())
                .summary(interviewDTO.getSummary())
                .build();

        Interview savedInterview = interviewRepository.save(interview);
        log.info("Interview created successfully with id: {}", savedInterview.getId());

        if (!CaseStatus.INVESTIGATING.name().equalsIgnoreCase(caseReport.getStatus().name())) {
            caseReport.setStatus(CaseStatus.valueOf(CaseStatus.INVESTIGATING.name()));
            caseReportRepository.save(caseReport);
        }

        return convertToDTO(savedInterview);
    }

    public InterviewDTO updateInterview(Long id, InterviewDTO interviewDTO) {
        log.info("Updating interview with id: {}", id);

        Interview existingInterview = interviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interview not found with id: " + id));

        // Update fields
        existingInterview.setIntervieweeName(interviewDTO.getIntervieweeName());
        existingInterview.setInterviewerName(interviewDTO.getInterviewerName());
        existingInterview.setInterviewDate(interviewDTO.getInterviewDate());
        existingInterview.setLocation(interviewDTO.getLocation());
        existingInterview.setSummary(interviewDTO.getSummary());

        Interview updatedInterview = interviewRepository.save(existingInterview);
        log.info("Interview updated successfully with id: {}", updatedInterview.getId());

        return convertToDTO(updatedInterview);
    }

    public void deleteInterview(Long id) {
        log.info("Deleting interview with id: {}", id);

        if (!interviewRepository.existsById(id)) {
            throw new RuntimeException("Interview not found with id: " + id);
        }

        interviewRepository.deleteById(id);
        log.info("Interview deleted successfully with id: {}", id);
    }

    public List<InterviewDTO> searchInterviewsByInterviewer(String interviewerName) {
        log.info("Searching interviews by interviewer: {}", interviewerName);
        return interviewRepository.findByInterviewerNameContainingIgnoreCase(interviewerName)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<InterviewDTO> searchInterviewsByInterviewee(String intervieweeName) {
        log.info("Searching interviews by interviewee: {}", intervieweeName);
        return interviewRepository.findByIntervieweeNameContainingIgnoreCase(intervieweeName)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private InterviewDTO convertToDTO(Interview interview) {
        return InterviewDTO.builder()
                .id(interview.getId())
                .caseId(interview.getCaseReport().getId())
                .intervieweeName(interview.getIntervieweeName())
                .interviewerName(interview.getInterviewerName())
                .interviewDate(interview.getInterviewDate())
                .location(interview.getLocation())
                .summary(interview.getSummary())
                .createdAt(interview.getCreatedAt())
                .updatedAt(interview.getUpdatedAt())
                .build();
    }
}