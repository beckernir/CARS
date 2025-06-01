package com.cars.child_abuse_reporting_system.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;


@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseReportDTO {
    // Reporter Information
    private String reporterFullName;
    private String abuserGender;
    private int abuserApproximateAge;
    private String relationship;

    // Child Information
    private String childFullName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate childDateOfBirth;

    private String childGender;
    private String abuseType;
    private String locationOfIncident;
    private boolean shareCurrentLocation;
    private Double latitude;
    private Double longitude;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfIncident;

    private String incidentDescription;

    // File attachments
    private MultipartFile voiceReport;
    private MultipartFile videoReport;
    private MultipartFile evidenceFile;

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

    public int getAbuserApproximateAge() {
        return abuserApproximateAge;
    }

    public void setAbuserApproximateAge(int abuserApproximateAge) {
        this.abuserApproximateAge = abuserApproximateAge;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
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

    public String getAbuseType() {
        return abuseType;
    }

    public void setAbuseType(String abuseType) {
        this.abuseType = abuseType;
    }

    public String getLocationOfIncident() {
        return locationOfIncident;
    }

    public void setLocationOfIncident(String locationOfIncident) {
        this.locationOfIncident = locationOfIncident;
    }

    public boolean isShareCurrentLocation() {
        return shareCurrentLocation;
    }

    public void setShareCurrentLocation(boolean shareCurrentLocation) {
        this.shareCurrentLocation = shareCurrentLocation;
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

    public MultipartFile getVoiceReport() {
        return voiceReport;
    }

    public void setVoiceReport(MultipartFile voiceReport) {
        this.voiceReport = voiceReport;
    }

    public MultipartFile getVideoReport() {
        return videoReport;
    }

    public void setVideoReport(MultipartFile videoReport) {
        this.videoReport = videoReport;
    }

    public MultipartFile getEvidenceFile() {
        return evidenceFile;
    }

    public void setEvidenceFile(MultipartFile evidenceFile) {
        this.evidenceFile = evidenceFile;
    }
}