package com.cars.child_abuse_reporting_system.viewControllers;

import com.cars.child_abuse_reporting_system.dtos.SummaryDTO;
import com.cars.child_abuse_reporting_system.entities.Summary;
import com.cars.child_abuse_reporting_system.services.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/cases")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;

    // GET request to render the overview page with modal
    @GetMapping("/case/overview")
    public String showOverview(Model model) {
        model.addAttribute("summary", new SummaryDTO()); // For modal form
        return "/admin/view-one";
    }
    /**
     * Delete an interview by ID.
     */
    @GetMapping("/delete/{id}")
    public String deleteInterview(@PathVariable String id,
                                  @PathVariable("caseId") Long caseId,
                                  @ModelAttribute("InterviewDto") SummaryDTO interviewDto,
                                  RedirectAttributes redirectAttributes) {
        interviewDto.setCaseId(caseId);
        summaryService.deleteSummary(id);
        return "redirect:/api/v1/admin/view/" + caseId;
    }

    // POST request to save the summary - Fixed URL mapping and parameter binding
    @PostMapping("/addSummary/{caseId}/create")
    public String saveSummary(@PathVariable("caseId") Long caseId,
                              @ModelAttribute("summaryDto") SummaryDTO summaryDto,
                              RedirectAttributes redirectAttributes) {
        try {
            // Set the case ID in the DTO
            summaryDto.setCaseId(caseId);

            // Save the summary using your service
            summaryService.createSummary(summaryDto);

            // Add a flash message for confirmation
            redirectAttributes.addFlashAttribute("successMessage", "Summary saved successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to save summary: " + e.getMessage());
        }

        // Redirect back to the case view page
        return "redirect:/api/v1/admin/view/" + caseId;
    }

    @GetMapping("/summaries/{caseId}")
    public String getSummaries(@PathVariable Long caseId, Model model) {
        model.addAttribute("summaries", summaryService.findByCaseId(caseId));
        return "cases/summaries :: summariesFragment";
    }

    @GetMapping("/summaries/edit/{id}")
    public String editSummaryForm(@PathVariable String id, Model model) {
        Summary summary = summaryService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid summary ID: " + id));

        SummaryDTO summaryDTO = SummaryDTO.builder()
                .id(summary.getId())
                .caseId(summary.getRelatedCase().getId())
                .summaryType(summary.getNoteType().name())
                .description(summary.getDescription())
                .incidentDetails(summary.getIncidentDetails())
                .initialAssessment(summary.getInitialAssessment())
                .currentUpdates(summary.getCurrentUpdates())
                .noteType(summary.getNoteType())
                .build();

        model.addAttribute("summaryDTO", summaryDTO);
        return "cases/edit-summary :: editSummaryForm";
    }

    @DeleteMapping("/summaries/delete/{id}")
    public ResponseEntity<?> deleteSummary(@PathVariable String id) {
        try {
            summaryService.deleteSummary(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Handle form-based delete for the modal
    @PostMapping("/summaries/{id}/delete")
    public String deleteSummaryForm(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            Summary summary = summaryService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid summary ID: " + id));
            Long caseId = summary.getRelatedCase().getId();

            summaryService.deleteSummary(id);
            redirectAttributes.addFlashAttribute("successMessage", "Summary deleted successfully");

            return "redirect:/api/v1/admin/view/" + caseId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete summary: " + e.getMessage());
            return "redirect:/api/v1/admin/view/";
        }
    }
}