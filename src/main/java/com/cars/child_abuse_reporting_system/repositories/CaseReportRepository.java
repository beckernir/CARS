package com.cars.child_abuse_reporting_system.repositories;

import com.cars.child_abuse_reporting_system.entities.CaseReport;
import com.cars.child_abuse_reporting_system.enums.CaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CaseReportRepository extends JpaRepository<CaseReport, Long> {

    /**
     * Find a case report by its unique case ID
     * @param caseId The case ID
     * @return An Optional containing the case report if found
     */
    Optional<CaseReport> findByCaseId(String caseId);

    /**
     * Find all case reports for a specific child
     * @param childName The name of the child (partial match, case-insensitive)
     * @return A list of matching case reports
     */
    List<CaseReport> findByChildFullNameContainingIgnoreCase(String childName);

    /**
     * Find all case reports submitted by a specific reporter
     * @param reporterName The name of the reporter (partial match, case-insensitive)
     * @return A list of matching case reports
     */
    List<CaseReport> findByReporterFullNameContainingIgnoreCase(String reporterName);

    /**
     * Find all case reports with a specific status
     * @param status The case status
     * @return A list of matching case reports
     */
    List<CaseReport> findByStatus(CaseStatus status);

    /**
     * Find all case reports within a specific date range
     * @param startDate The start date
     * @param endDate The end date
     * @return A list of matching case reports
     */
    List<CaseReport> findByDateOfIncidentBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find all case reports from a specific geographic area
     * @param latitudeMin The minimum latitude
     * @param latitudeMax The maximum latitude
     * @param longitudeMin The minimum longitude
     * @param longitudeMax The maximum longitude
     * @return A list of matching case reports
     */
    @Query("SELECT c FROM CaseReport c WHERE c.latitude BETWEEN :latMin AND :latMax AND c.longitude BETWEEN :lonMin AND :lonMax")
    List<CaseReport> findByGeographicArea(
            @Param("latMin") Double latitudeMin,
            @Param("latMax") Double latitudeMax,
            @Param("lonMin") Double longitudeMin,
            @Param("lonMax") Double longitudeMax);

    /**
     * Find all case reports with a specific abuse type
     * @param abuseType The abuse type
     * @return A list of matching case reports
     */
    List<CaseReport> findByAbuseType(String abuseType);

    /**
     * Find all reports that need immediate attention (recent incidents)
     * @param thresholdDate Reports with incident dates on or after this date
     * @return A list of matching case reports
     */
    @Query("SELECT c FROM CaseReport c WHERE c.dateOfIncident >= :thresholdDate AND c.status = 'REPORTED'")
    List<CaseReport> findRecentReports(@Param("thresholdDate") LocalDate thresholdDate);

    /**
     * Count reports by status
     * @return A count of reports grouped by status
     */
    @Query("SELECT c.status, COUNT(c) FROM CaseReport c GROUP BY c.status")
    List<Object[]> countByStatus();

    /**
     * Find all reports with advanced search criteria
     * @param childName Optional child name
     * @param reporterName Optional reporter name
     * @param status Optional status
     * @param startDate Optional start date
     * @param endDate Optional end date
     * @param pageable Pagination information
     * @return A page of matching case reports
     */
    @Query("SELECT c FROM CaseReport c WHERE " +
            "(:childName IS NULL OR LOWER(c.childFullName) LIKE LOWER(CONCAT('%', :childName, '%'))) AND " +
            "(:reporterName IS NULL OR LOWER(c.reporterFullName) LIKE LOWER(CONCAT('%', :reporterName, '%'))) AND " +
            "(:status IS NULL OR c.status = :status) AND " +
            "(:startDate IS NULL OR c.dateOfIncident >= :startDate) AND " +
            "(:endDate IS NULL OR c.dateOfIncident <= :endDate)")
    Page<CaseReport> findByAdvancedSearch(
            @Param("childName") String childName,
            @Param("reporterName") String reporterName,
            @Param("status") CaseStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);


    // New methods for analytics

    /**
     * Find cases by multiple statuses (for active/closed filtering)
     */
    List<CaseReport> findByStatusIn(List<CaseStatus> statuses);

    /**
     * Find cases reported after a specific date (for monthly trends)
     */
    List<CaseReport> findByReportDateAfter(LocalDateTime date);

    /**
     * Count cases by status
     */
    long countByStatus(CaseStatus status);

    /**
     * Count cases by multiple statuses
     */
    long countByStatusIn(List<CaseStatus> statuses);

    /**
     * Get cases within a date range
     */
    @Query("SELECT c FROM CaseReport c WHERE c.reportDate BETWEEN :startDate AND :endDate")
    List<CaseReport> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    /**
     * Get cases by location containing specific text
     */
    List<CaseReport> findByLocationOfIncidentContainingIgnoreCase(String location);

    /**
     * Get recent emergency cases (for dashboard alerts)
     */
    @Query("SELECT c FROM CaseReport c WHERE c.dateOfIncident >= :recentDate AND " +
            "(c.abuseType = 'PHYSICAL' OR c.abuseType = 'SEXUAL') ")
    List<CaseReport> findRecentEmergencyCases(@Param("recentDate") java.time.LocalDate recentDate);

    /**
     * Count total cases
     */
    @Query("SELECT COUNT(c) FROM CaseReport c")
    long getTotalCaseCount();

    /**
     * Get cases grouped by month for statistical analysis
     */
    @Query("SELECT YEAR(c.reportDate) as year, MONTH(c.reportDate) as month, COUNT(c) as count " +
            "FROM CaseReport c WHERE c.reportDate >= :startDate " +
            "GROUP BY YEAR(c.reportDate), MONTH(c.reportDate) " +
            "ORDER BY year DESC, month DESC")
    List<Object[]> getCaseCountByMonth(@Param("startDate") LocalDateTime startDate);

    /**
     * Get cases by age range
     */
    @Query("SELECT c FROM CaseReport c WHERE " +
            "YEAR(CURRENT_DATE) - YEAR(c.childDateOfBirth) BETWEEN :minAge AND :maxAge")
    List<CaseReport> findByChildAgeRange(@Param("minAge") int minAge, @Param("maxAge") int maxAge);


    /**
     * Count active cases by user ID (custom query)
     */
    @Query("SELECT COUNT(c) FROM CaseReport c WHERE c.createdByUserId = :userId AND c.status IN :statuses")
    long countActiveByUserId(@Param("userId") String userId, @Param("statuses") List<CaseStatus> statuses);
    /**
     * Count total cases by user ID
     */
    long countByCreatedByUserId(String userId);
    /**
     * Find all cases created by a specific user
     */
    List<CaseReport> findByCreatedByUserId(String userId);



}