package com.cars.child_abuse_reporting_system.dtos;

import com.cars.child_abuse_reporting_system.enums.NoteType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SummaryDTO {

    private String id;

    @NotBlank(message = "Case ID is required")
    private Long caseId;

    @NotBlank(message = "Summary type is required")
    private String summaryType;

    private String description;

    private String incidentDetails;

    private String initialAssessment;

    private String currentUpdates;

    @NotNull(message = "Note type is required")
    private NoteType noteType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public @NotBlank(message = "Case ID is required") Long getCaseId() {
        return caseId;
    }

    public void setCaseId(@NotBlank(message = "Case ID is required") Long caseId) {
        this.caseId = caseId;
    }

    public @NotBlank(message = "Summary type is required") String getSummaryType() {
        return summaryType;
    }

    public void setSummaryType(@NotBlank(message = "Summary type is required") String summaryType) {
        this.summaryType = summaryType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIncidentDetails() {
        return incidentDetails;
    }

    public void setIncidentDetails(String incidentDetails) {
        this.incidentDetails = incidentDetails;
    }

    public String getInitialAssessment() {
        return initialAssessment;
    }

    public void setInitialAssessment(String initialAssessment) {
        this.initialAssessment = initialAssessment;
    }

    public String getCurrentUpdates() {
        return currentUpdates;
    }

    public void setCurrentUpdates(String currentUpdates) {
        this.currentUpdates = currentUpdates;
    }

    public @NotNull(message = "Note type is required") NoteType getNoteType() {
        return noteType;
    }

    public void setNoteType(@NotNull(message = "Note type is required") NoteType noteType) {
        this.noteType = noteType;
    }
}