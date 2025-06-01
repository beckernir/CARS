package com.cars.child_abuse_reporting_system.services;

import com.cars.child_abuse_reporting_system.dtos.*;
import com.cars.child_abuse_reporting_system.entities.CaseReport;
import com.cars.child_abuse_reporting_system.enums.AbuseType;
import com.cars.child_abuse_reporting_system.enums.CaseStatus;
import com.cars.child_abuse_reporting_system.exceptions.CaseNotFoundException;
import com.cars.child_abuse_reporting_system.exceptions.FileProcessingException;
import com.cars.child_abuse_reporting_system.exceptions.InvalidRequestException;
import com.cars.child_abuse_reporting_system.exceptions.ResourceNotFoundException;
import com.cars.child_abuse_reporting_system.repositories.CaseReportRepository;
import com.cars.child_abuse_reporting_system.utils.CaseIdGeneratorService;
import com.cars.child_abuse_reporting_system.utils.FileStorageService;
import com.cars.child_abuse_reporting_system.utils.GeoLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Service
public class CaseReportService {

    @Autowired
    private CaseReportRepository reportRepository;

    @Autowired
    private CaseIdGeneratorService caseIdGeneratorService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private GeoLocationService geoLocationService;
    @Autowired
    private CaseReportRepository caseReportRepository;

    public List<CaseReport> getAll(){
        return reportRepository.findAll();
    }

    public CaseReport getCaseReportById(Long id) {
        return caseReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case Report not found with id: " + id));
    }
    /**
     * Create a new case report
     * @param reportDTO The report data transfer object
     * @return The generated case ID
     * @throws InvalidRequestException If the request contains invalid data
     * @throws FileProcessingException If there is an error processing uploaded files
     */
    @Transactional
    public String submitReport(CaseReportDTO reportDTO,String userId) {
        validateReportData(reportDTO);

        CaseReport report = createReportFromDTO(reportDTO);
        report.setReportDate(LocalDateTime.now());
        report.setStatus(CaseStatus.REPORTED);
        report.setCreatedByUserId(userId); // Set the user who created this case


        // Generate unique case ID
        String caseId = caseIdGeneratorService.generateCaseId();
        report.setCaseId(caseId);

        // Handle file uploads with validation
        processFileUploads(reportDTO, report);

        // Process location data if current location is shared
        if (reportDTO.isShareCurrentLocation() && reportDTO.getLatitude() != null && reportDTO.getLongitude() != null) {
            processLocationData(reportDTO, report);
        }

        CaseReport savedReport = reportRepository.save(report);

        // Notify relevant agencies if emergency situation detected
        if (isEmergencySituation(reportDTO)) {
            notifyEmergencyServices(savedReport);
        }

        return caseId;
    }

    /**
     * Retrieve a case report by its unique case ID
     * @param caseId The unique case ID
     * @return The case report
     * @throws CaseNotFoundException If the case cannot be found
     */
    public CaseReport getReportByCaseId(String caseId) {
//        return reportRepository.findByCaseId(caseId)
        return reportRepository.findByCaseId(caseId)

                .orElseThrow(() -> new CaseNotFoundException("Case not found with ID: " + caseId));
    }

    /**
     * Retrieve all case reports with pagination
     * @param pageable Pagination information
     * @return A page of case reports
     */
    public Page<CaseReport> getAllReports(Pageable pageable) {
        return reportRepository.findAll(pageable);
    }

    /**
     * Update an existing case report
     * @param caseId The unique case ID
     * @param reportDTO The updated report data
     * @return The updated case report
     * @throws CaseNotFoundException If the case cannot be found
     * @throws InvalidRequestException If the request contains invalid data
     * @throws FileProcessingException If there is an error processing uploaded files
     */
    @Transactional
    public CaseReport updateReport(String caseId, CaseReportDTO reportDTO) {
        CaseReport existingReport = getReportByCaseId(caseId);
        validateReportData(reportDTO);

        // Update basic info
        updateReportFields(existingReport, reportDTO);

        // Process new file uploads if provided
        if (reportDTO.getVoiceReport() != null && !reportDTO.getVoiceReport().isEmpty()) {
            // Delete old file if exists
            String voiceUrl = fileStorageService.processVoiceReport(reportDTO.getVoiceReport());
            existingReport.setVoiceReportPath(voiceUrl);
        }

        if (reportDTO.getVideoReport() != null && !reportDTO.getVideoReport().isEmpty()) {
            // Delete old file if exists
            String videoUrl = fileStorageService.processVideoReport(reportDTO.getVideoReport());
            existingReport.setVideoReportPath(videoUrl);
        }

        if (reportDTO.getEvidenceFile() != null && !reportDTO.getEvidenceFile().isEmpty()) {
            // Delete old file if exists
            String evidenceUrl = fileStorageService.processEvidenceFile(reportDTO.getEvidenceFile());
            existingReport.setEvidenceFilePath(evidenceUrl);
        }

        // Update location if needed
        if (reportDTO.isShareCurrentLocation() && reportDTO.getLatitude() != null && reportDTO.getLongitude() != null) {
            processLocationData(reportDTO, existingReport);
        }

        return reportRepository.save(existingReport);
    }

    /**
     * Update the status of a case report
     * @param caseId The unique case ID
     * @param newStatus The new status
     * @return The updated case report
     * @throws CaseNotFoundException If the case cannot be found
     */
    @Transactional
    public CaseReport updateReportStatus(String caseId, CaseStatus newStatus) {
        CaseReport report = getReportByCaseId(caseId);
        report.setStatus(newStatus);
        return reportRepository.save(report);
    }

    /**
     * Delete a case report
     * @param caseId The unique case ID
     * @throws CaseNotFoundException If the case cannot be found
     */
    @Transactional
    public void deleteReport(String caseId) {
        CaseReport report = getReportByCaseId(caseId);

        reportRepository.delete(report);
    }

    /**
     * Search for case reports by child name
     * @param childName The name of the child
     * @return A list of matching case reports
     */
    public List<CaseReport> searchByChildName(String childName) {
        return reportRepository.findByChildFullNameContainingIgnoreCase(childName);
    }

    /**
     * Search for case reports by reporter name
     * @param reporterName The name of the reporter
     * @return A list of matching case reports
     */
    public List<CaseReport> searchByReporterName(String reporterName) {
        return reportRepository.findByReporterFullNameContainingIgnoreCase(reporterName);
    }

    /**
     * Search for case reports by status
     * @param status The case status
     * @return A list of matching case reports
     */
    public List<CaseReport> searchByStatus(CaseStatus status) {
        return reportRepository.findByStatus(status);
    }

    /**
     * Validate the input data
     * @param reportDTO The data to validate
     * @throws InvalidRequestException If the data is invalid
     */
    private void validateReportData(CaseReportDTO reportDTO) {
        if (reportDTO.getChildFullName() == null || reportDTO.getChildFullName().trim().isEmpty()) {
            throw new InvalidRequestException("Child's full name is required");
        }

        if (reportDTO.getChildDateOfBirth() == null) {
            throw new InvalidRequestException("Child's date of birth is required");
        }

        if (reportDTO.getAbuseType() == null || reportDTO.getAbuseType().trim().isEmpty()) {
            throw new InvalidRequestException("Abuse type is required");
        }

        try {
            AbuseType.valueOf(reportDTO.getAbuseType());
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Invalid abuse type: " + reportDTO.getAbuseType());
        }

        if (reportDTO.getDateOfIncident() == null) {
            throw new InvalidRequestException("Date of incident is required");
        }

        if (reportDTO.getIncidentDescription() == null || reportDTO.getIncidentDescription().trim().isEmpty()) {
            throw new InvalidRequestException("Incident description is required");
        }

        if (!reportDTO.isShareCurrentLocation() &&
                (reportDTO.getLocationOfIncident() == null || reportDTO.getLocationOfIncident().trim().isEmpty())) {
            throw new InvalidRequestException("Location of incident is required if current location is not shared");
        }
    }

    private void updateReportFields(CaseReport report, CaseReportDTO dto) {
        // Reporter Information
        report.setReporterFullName(dto.getReporterFullName());
        report.setAbuserGender(dto.getAbuserGender());
        report.setAbuserApproximateAge(dto.getAbuserApproximateAge());
        report.setRelationship(dto.getRelationship());

        // Child Information
        report.setChildFullName(dto.getChildFullName());
        report.setChildDateOfBirth(dto.getChildDateOfBirth());
        report.setChildGender(dto.getChildGender());
        report.setAbuseType(AbuseType.valueOf(dto.getAbuseType()));
        report.setLocationOfIncident(dto.getLocationOfIncident());
        report.setDateOfIncident(dto.getDateOfIncident());
        report.setIncidentDescription(dto.getIncidentDescription());
        report.setUsedCurrentLocation(dto.isShareCurrentLocation());
    }

    private boolean isEmergencySituation(CaseReportDTO reportDTO) {
        // Logic to determine if this is an emergency situation
        // For example: if the incident is very recent or involves severe physical/sexual abuse
        if (reportDTO.getDateOfIncident() != null) {
            LocalDate recentThreshold = LocalDate.now().minusDays(2);
            if (reportDTO.getDateOfIncident().isAfter(recentThreshold)) {
                if ("PHYSICAL".equals(reportDTO.getAbuseType()) || "SEXUAL".equals(reportDTO.getAbuseType())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void notifyEmergencyServices(CaseReport report) {
        // Implementation to notify emergency services
        // This could be an SMS alert, email, or API call to emergency services
        geoLocationService.shareLocationWithAuthorities(
                report.getCaseId(),
                report.getLocationOfIncident(),
                report.getLatitude(),
                report.getLongitude()
        );
    }

    private void processLocationData(CaseReportDTO dto, CaseReport report) {
        report.setLatitude(dto.getLatitude());
        report.setLongitude(dto.getLongitude());
        report.setUsedCurrentLocation(true);

        // If we need to update the location description based on coordinates
        if (dto.getLocationOfIncident() == null || dto.getLocationOfIncident().isEmpty()) {
            String addressFromCoordinates = geoLocationService.getAddressFromCoordinates(
                    dto.getLatitude(),
                    dto.getLongitude()
            );
            report.setLocationOfIncident(addressFromCoordinates);
        }
    }

    private CaseReport createReportFromDTO(CaseReportDTO dto) {
        CaseReport report = new CaseReport();

        // Reporter Information
        report.setReporterFullName(dto.getReporterFullName());
        report.setAbuserGender(dto.getAbuserGender());
        report.setAbuserApproximateAge(dto.getAbuserApproximateAge());
        report.setRelationship(dto.getRelationship());

        // Child Information
        report.setChildFullName(dto.getChildFullName());
        report.setChildDateOfBirth(dto.getChildDateOfBirth());
        report.setChildGender(dto.getChildGender());
        report.setAbuseType(AbuseType.valueOf(dto.getAbuseType()));
        report.setLocationOfIncident(dto.getLocationOfIncident());
        report.setDateOfIncident(dto.getDateOfIncident());
        report.setIncidentDescription(dto.getIncidentDescription());
        report.setUsedCurrentLocation(dto.isShareCurrentLocation());

        return report;
    }

    private void processFileUploads(CaseReportDTO dto, CaseReport report) {
        try {
            // Process and validate voice report
            if (dto.getVoiceReport() != null && !dto.getVoiceReport().isEmpty()) {
                String voiceUrl = fileStorageService.processVoiceReport(dto.getVoiceReport());
                report.setVoiceReportPath(voiceUrl);
            }

            // Process and validate video report
            if (dto.getVideoReport() != null && !dto.getVideoReport().isEmpty()) {
                String videoUrl = fileStorageService.processVideoReport(dto.getVideoReport());
                report.setVideoReportPath(videoUrl);
            }

            // Process and validate evidence file
            if (dto.getEvidenceFile() != null && !dto.getEvidenceFile().isEmpty()) {
                String evidenceUrl = fileStorageService.processEvidenceFile(dto.getEvidenceFile());
                report.setEvidenceFilePath(evidenceUrl);
            }
        } catch (Exception e) {
            throw new FileProcessingException("Failed to process and upload files: " + e.getMessage(), e);
        }
    }
    /**
     * Get database ID by public case ID
     * This method converts the public-facing case ID (e.g., CA-20240515-AB12)
     * to the internal database ID
     */
    public Long getDatabaseIdByPublicCaseId(String publicCaseId) {
        if (publicCaseId == null || publicCaseId.trim().isEmpty()) {
            return null;
        }

//        // Clean and validate the case ID format
//        String cleanCaseId = publicCaseId.trim().toUpperCase();
//        if (!isValidCaseIdFormat(cleanCaseId)) {
//            return null;
//        }

        try {
            Optional<CaseReport> caseReport = caseReportRepository.findByCaseId(publicCaseId);
            return caseReport != null ? caseReport.get().getId() : null;
        } catch (Exception e) {
            // Log the error but don't expose internal details
            return null;
        }
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    private String categorizeAge(int age) {
        if (age >= 0 && age <= 2) return "0-2 years";
        if (age >= 3 && age <= 5) return "3-5 years";
        if (age >= 6 && age <= 8) return "6-8 years";
        if (age >= 9 && age <= 12) return "9-12 years";
        if (age >= 13 && age <= 15) return "13-15 years";
        if (age >= 16 && age <= 18) return "16-18 years";
        return "Unknown";
    }

    private String extractRegionFromLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            return "Unknown";
        }

        // Simple region extraction - you can enhance this based on your needs
        String[] parts = location.split(",");
        if (parts.length > 0) {
            return parts[parts.length - 1].trim(); // Last part usually contains region/state
        }

        return location.trim();
    }

    // ==================== NEW ANALYTICS METHODS ====================

    /**
     * Get list of active cases (REPORTED, UNDER_INVESTIGATION, IN_PROGRESS)
     */
    public List<CaseReport> getActiveCases() {
        List<CaseStatus> activeStatuses = Arrays.asList(
                CaseStatus.REPORTED
//                CaseStatus.UNDER_INVESTIGATION, CaseStatus.IN_PROGRESS
        );
        return reportRepository.findByStatusIn(activeStatuses);
    }

    /**
     * Get list of closed cases (RESOLVED, CLOSED, DISMISSED)
     */
    public List<CaseReport> getClosedCases() {
        List<CaseStatus> closedStatuses = Arrays.asList(
                CaseStatus.RESOLVED, CaseStatus.CLOSED
//                CaseStatus.DISMISSED
        );
        return reportRepository.findByStatusIn(closedStatuses);
    }

    /**
     * Get comprehensive case statistics dashboard
     */
    public CaseStatisticsDTO getCaseStatistics() {
        List<CaseReport> allCases = reportRepository.findAll();

        long totalCases = allCases.size();
        long activeCases = getActiveCases().size();
        long closedCases = getClosedCases().size();

        return CaseStatisticsDTO.builder()
                .totalCases(totalCases)
                .activeCases(activeCases)
                .closedCases(closedCases)
                .build();
    }

    /**
     * Analyze cases by children age groups
     */
    public List<AgeGroupAnalysisDTO> getAgeGroupAnalysis() {
        List<CaseReport> allCases = reportRepository.findAll();
        long totalCases = allCases.size();

        if (totalCases == 0) {
            return new ArrayList<>();
        }

        Map<String, Long> ageGroups = new HashMap<>();

        for (CaseReport caseReport : allCases) {
            if (caseReport.getChildDateOfBirth() != null) {
                int age = calculateAge(caseReport.getChildDateOfBirth());
                String ageGroup = categorizeAge(age);
                ageGroups.merge(ageGroup, 1L, Long::sum);
            }
        }

        return ageGroups.entrySet().stream()
                .map(entry -> {
                    double percentage = (entry.getValue() * 100.0) / totalCases;
                    return AgeGroupAnalysisDTO.builder()
                            .ageGroup(entry.getKey())
                            .totalCases(entry.getValue())
                            .percentage(Math.round(percentage * 100.0) / 100.0)
                            .build();
                })
                .sorted(Comparator.comparing(AgeGroupAnalysisDTO::getAgeGroup))
                .collect(Collectors.toList());
    }

    /**
     * Analyze cases by different regions/locations
     */
    public List<RegionAnalysisDTO> getRegionAnalysis() {
        List<CaseReport> allCases = reportRepository.findAll();
        long totalCases = allCases.size();

        if (totalCases == 0) {
            return new ArrayList<>();
        }

        Map<String, Long> regionCounts = new HashMap<>();

        for (CaseReport caseReport : allCases) {
            String region = extractRegionFromLocation(caseReport.getLocationOfIncident());
            if (region != null && !region.trim().isEmpty()) {
                regionCounts.merge(region, 1L, Long::sum);
            }
        }

        return regionCounts.entrySet().stream()
                .map(entry -> {
                    double percentage = (entry.getValue() * 100.0) / totalCases;
                    return RegionAnalysisDTO.builder()
                            .region(entry.getKey())
                            .totalCases(entry.getValue())
                            .percentage(Math.round(percentage * 100.0) / 100.0)
                            .build();
                })
                .sorted(Comparator.comparing(RegionAnalysisDTO::getTotalCases).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get comprehensive dashboard data
     */
    public DashboardAnalyticsDTO getDashboardAnalytics() {
        return DashboardAnalyticsDTO.builder()
                .caseStatistics(getCaseStatistics())
                .ageGroupAnalysis(getAgeGroupAnalysis())
                .regionAnalysis(getRegionAnalysis())
                .abuseTypeAnalysis(getAbuseTypeAnalysis())
                .monthlyTrends(getMonthlyTrends())
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    /**
     * Analyze cases by abuse type
     */
    public List<AbuseTypeAnalysisDTO> getAbuseTypeAnalysis() {
        List<CaseReport> allCases = reportRepository.findAll();
        long totalCases = allCases.size();

        if (totalCases == 0) {
            return new ArrayList<>();
        }

        Map<AbuseType, Long> abuseTypeCounts = allCases.stream()
                .filter(caseReport -> caseReport.getAbuseType() != null)
                .collect(Collectors.groupingBy(
                        CaseReport::getAbuseType,
                        Collectors.counting()
                ));

        return abuseTypeCounts.entrySet().stream()
                .map(entry -> {
                    double percentage = (entry.getValue() * 100.0) / totalCases;
                    return AbuseTypeAnalysisDTO.builder()
                            .abuseType(entry.getKey().name())
                            .totalCases(entry.getValue())
                            .percentage(Math.round(percentage * 100.0) / 100.0)
                            .build();
                })
                .sorted(Comparator.comparing(AbuseTypeAnalysisDTO::getTotalCases).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get monthly trends for the last 12 months
     */
    public List<MonthlyTrendDTO> getMonthlyTrends() {
        LocalDateTime twelveMonthsAgo = LocalDateTime.now().minusMonths(12);
        List<CaseReport> recentCases = reportRepository.findByReportDateAfter(twelveMonthsAgo);

        Map<String, Long> monthlyData = recentCases.stream()
                .collect(Collectors.groupingBy(
                        caseReport -> caseReport.getReportDate().getYear() + "-" +
                                String.format("%02d", caseReport.getReportDate().getMonthValue()),
                        Collectors.counting()
                ));

        return monthlyData.entrySet().stream()
                .map(entry -> MonthlyTrendDTO.builder()
                        .month(entry.getKey())
                        .totalCases(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(MonthlyTrendDTO::getMonth))
                .collect(Collectors.toList());
    }

    /**
     * Get all cases created by a specific user
     * @param userId The ID of the logged-in user
     * @return List of case reports created by the user
     */
    public List<CaseReport> getCasesByUserId(String userId) {
        return reportRepository.findByCreatedByUserId(userId);
    }

}

