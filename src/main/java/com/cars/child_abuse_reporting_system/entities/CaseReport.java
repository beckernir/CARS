package com.cars.child_abuse_reporting_system.entities;

import com.cars.child_abuse_reporting_system.enums.AbuseType;
import com.cars.child_abuse_reporting_system.enums.CaseStatus;
import com.cars.child_abuse_reporting_system.enums.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "case_reports_tbl")
public class CaseReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String caseId;

    // Reporter Information (can be anonymous)
    @Column(nullable = false, length = 100)
    private String reporterFullName;
    private String abuserGender;
    private int abuserApproximateAge;
    private String relationship;

    // Child Information
    private String childFullName;
    private LocalDate childDateOfBirth;
    private String childGender;

    @Enumerated(EnumType.STRING)
    private AbuseType abuseType;

    // Location
    private String locationOfIncident;
    private LocalDate dateOfIncident;
    private String incidentDescription;
    private boolean usedCurrentLocation;
    private Double latitude;
    private Double longitude;

    // File attachments - store file paths
    private String voiceReportPath;
    private String videoReportPath;
    private String evidenceFilePath;

    private LocalDateTime reportDate;

    @Enumerated(EnumType.STRING)
    private CaseStatus status;

    @Column(name = "created_by_user_id")
    private String createdByUserId;

    @OneToMany(mappedBy = "relatedCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Summary> summaries;

    public String getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(String createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public Long getId() {
        return id;
    }

    public List<Summary> getSummaries() {
        return summaries;
    }

    public void setSummaries(List<Summary> summaries) {
        this.summaries = summaries;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getReporterFullName() {
        return reporterFullName;
    }

    public void setReporterFullName(String reporterFullName) {
        this.reporterFullName = reporterFullName;
    }

    public String getAbuserGender() {
        return abuserGender;
    }

    public void setAbuserGender(String abuserGender) {
        this.abuserGender = abuserGender;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public int getAbuserApproximateAge() {
        return abuserApproximateAge;
    }

    public void setAbuserApproximateAge(int abuserApproximateAge) {
        this.abuserApproximateAge = abuserApproximateAge;
    }

    public String getChildFullName() {
        return childFullName;
    }

    public void setChildFullName(String childFullName) {
        this.childFullName = childFullName;
    }

    public LocalDate getChildDateOfBirth() {
        return childDateOfBirth;
    }

    public void setChildDateOfBirth(LocalDate childDateOfBirth) {
        this.childDateOfBirth = childDateOfBirth;
    }

    public String getChildGender() {
        return childGender;
    }

    public void setChildGender(String childGender) {
        this.childGender = childGender;
    }

    public AbuseType getAbuseType() {
        return abuseType;
    }

    public void setAbuseType(AbuseType abuseType) {
        this.abuseType = abuseType;
    }

    public String getLocationOfIncident() {
        return locationOfIncident;
    }

    public void setLocationOfIncident(String locationOfIncident) {
        this.locationOfIncident = locationOfIncident;
    }

    public LocalDate getDateOfIncident() {
        return dateOfIncident;
    }

    public void setDateOfIncident(LocalDate dateOfIncident) {
        this.dateOfIncident = dateOfIncident;
    }

    public String getIncidentDescription() {
        return incidentDescription;
    }

    public void setIncidentDescription(String incidentDescription) {
        this.incidentDescription = incidentDescription;
    }

    public boolean isUsedCurrentLocation() {
        return usedCurrentLocation;
    }

    public void setUsedCurrentLocation(boolean usedCurrentLocation) {
        this.usedCurrentLocation = usedCurrentLocation;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getVoiceReportPath() {
        return voiceReportPath;
    }

    public void setVoiceReportPath(String voiceReportPath) {
        this.voiceReportPath = voiceReportPath;
    }

    public String getVideoReportPath() {
        return videoReportPath;
    }

    public void setVideoReportPath(String videoReportPath) {
        this.videoReportPath = videoReportPath;
    }

    public String getEvidenceFilePath() {
        return evidenceFilePath;
    }

    public void setEvidenceFilePath(String evidenceFilePath) {
        this.evidenceFilePath = evidenceFilePath;
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public CaseStatus getStatus() {
        return status;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
    }
}