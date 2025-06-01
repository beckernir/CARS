package com.cars.child_abuse_reporting_system.repositories;

import com.cars.child_abuse_reporting_system.entities.CaseAssignment;
import com.cars.child_abuse_reporting_system.entities.CaseReport;
import com.cars.child_abuse_reporting_system.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseAssignmentRepository extends JpaRepository<CaseAssignment, Long> {
    List<CaseAssignment> findByAssignedCaseId(Long caseId);

    @Query("SELECT ca.assignedCase FROM CaseAssignment ca WHERE ca.authorityRole = :role")
    List<CaseReport> findCasesByRole(@Param("role") Role role);
}
