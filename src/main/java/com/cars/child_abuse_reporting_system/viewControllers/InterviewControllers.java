package com.cars.child_abuse_reporting_system.viewControllers;

import com.cars.child_abuse_reporting_system.dtos.InterviewDTO;
import com.cars.child_abuse_reporting_system.entities.Interview;
import com.cars.child_abuse_reporting_system.services.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/interview")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InterviewControllers {

    private final InterviewService interviewService;

    /**
     * Get all interviews
     */
    @GetMapping
    public ResponseEntity<List<Interview>> getAllInterviews() {
        return ResponseEntity.ok(interviewService.getAllInterviews());
    }

    /**
     * Get interviews for a specific case
     */
    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<InterviewDTO>> getInterviewsByCaseId(@PathVariable Long caseId) {
        return ResponseEntity.ok(interviewService.getInterviewsByCaseId(caseId));
    }

    /**
     * Get a specific interview by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Interview> getInterviewById(@PathVariable Long id) {
        Optional<Interview> interview = interviewService.getInterviewById(id);
        return interview.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new interview
     */
    @PostMapping
    public ResponseEntity<InterviewDTO> createInterview(@Valid @RequestBody InterviewDTO interviewDTO) {
        try {
            InterviewDTO createdInterview = interviewService.createInterview(interviewDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdInterview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Create a new interview for a specific case
     */
    @PostMapping("/case/{caseId}")
    public ResponseEntity<InterviewDTO> createInterviewForCase(
            @PathVariable Long caseId,
            @Valid @RequestBody InterviewDTO interviewDTO) {
        try {
            interviewDTO.setCaseId(caseId);
            InterviewDTO createdInterview = interviewService.createInterview(interviewDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdInterview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update an interview
     */
    @PutMapping("/{id}")
    public ResponseEntity<InterviewDTO> updateInterview(
            @PathVariable Long id,
            @Valid @RequestBody InterviewDTO interviewDTO) {
        try {
            InterviewDTO updatedInterview = interviewService.updateInterview(id, interviewDTO);
            return ResponseEntity.ok(updatedInterview);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete an interview
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInterview(@PathVariable Long id) {
        try {
            interviewService.deleteInterview(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Search interviews by interviewer name
     */
    @GetMapping("/search/interviewer")
    public ResponseEntity<List<InterviewDTO>> searchByInterviewer(@RequestParam String name) {
        return ResponseEntity.ok(interviewService.searchInterviewsByInterviewer(name));
    }

    /**
     * Search interviews by interviewee name
     */
    @GetMapping("/search/interviewee")
    public ResponseEntity<List<InterviewDTO>> searchByInterviewee(@RequestParam String name) {
        return ResponseEntity.ok(interviewService.searchInterviewsByInterviewee(name));
    }
}
