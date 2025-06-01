package com.cars.child_abuse_reporting_system.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewDTO {
    private Long id;

    @NotNull(message = "Case ID is required")
    private Long caseId;

    @NotBlank(message = "Interviewee name is required")
    private String intervieweeName;

    @NotBlank(message = "Interviewer name is required")
    private String interviewerName;

    @NotNull(message = "Interview date is required")
    private LocalDateTime interviewDate;

    private String location;
    private String summary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}