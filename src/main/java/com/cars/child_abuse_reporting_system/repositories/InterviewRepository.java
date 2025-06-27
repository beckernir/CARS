package com.cars.child_abuse_reporting_system.repositories;

import com.cars.child_abuse_reporting_system.dtos.InterviewDTO;
import com.cars.child_abuse_reporting_system.entities.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface InterviewRepository extends JpaRepository<Interview, Long> {
    @Query("SELECT i FROM Interview i WHERE i.caseReport.id = :caseId ORDER BY i.interviewDate DESC")
    List<Interview> findByCaseReportIdOrderByInterviewDateDesc(@Param("caseId") Long caseId);

    List<Interview> findByInterviewerNameContainingIgnoreCase(String interviewerName);

    List<Interview> findByIntervieweeNameContainingIgnoreCase(String intervieweeName);

}