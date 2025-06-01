package com.cars.child_abuse_reporting_system.repositories;

import com.cars.child_abuse_reporting_system.entities.Summary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SummaryRepository extends JpaRepository<Summary, String> {
    List<Summary> findByRelatedCaseId(Long caseId);
}