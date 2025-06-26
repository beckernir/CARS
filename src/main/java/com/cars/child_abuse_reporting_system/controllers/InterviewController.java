package com.cars.child_abuse_reporting_system.controllers;

import com.cars.child_abuse_reporting_system.dtos.InterviewDTO;
import com.cars.child_abuse_reporting_system.services.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/v1/interview")
@RequiredArgsConstructor
public class InterviewController {

    @Autowired
    private final InterviewService interviewService;


    @PostMapping("/{caseId}")
    public String saveInterview(@PathVariable("caseId") Long caseId,
                              @ModelAttribute("InterviewDto") InterviewDTO interviewDto,
                              RedirectAttributes redirectAttributes) {
        try {
            // Set the case ID in the DTO
            interviewDto.setCaseId(caseId);

            // Save the Interview using your service
            interviewService.createInterview(interviewDto);

            // Add a flash message for confirmation
            redirectAttributes.addFlashAttribute("successMessage", "Interview saved successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to save Interview: " + e.getMessage());
        }

        // Redirect back to the case view page
        return "redirect:/api/v1/admin/view/" + caseId;
    }
    /**
     * Delete an interview by ID.
     */
    @GetMapping("/delete/{id}")
    public String deleteInterview(@PathVariable Long id,
                                  @PathVariable("caseId") Long caseId,
                                  @ModelAttribute("InterviewDto") InterviewDTO interviewDto,
                                  RedirectAttributes redirectAttributes) {
        interviewDto.setCaseId(caseId);
        interviewService.deleteInterview(id);
        return "redirect:/api/v1/admin/view/" + caseId;
    }
}
