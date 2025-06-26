package com.cars.child_abuse_reporting_system.entities;

import com.cars.child_abuse_reporting_system.enums.PriorityLevel;
import com.cars.child_abuse_reporting_system.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CaseAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "case_id", nullable = false)
    private CaseReport assignedCase;

    // This allows one case to be assigned to multiple roles
    @Enumerated(EnumType.STRING)
//    @ElementCollection(targetClass = Role.class)
//    @CollectionTable(name = "case_role_assignments",
//            joinColumns = @JoinColumn(name = "case_id"))
//    @Column(name = "role")
    private Role authorityRole ;

    private String assignmentNotes;

    @Enumerated(EnumType.STRING)
    private PriorityLevel priorityLevel;

    private LocalDateTime assignedAt = LocalDateTime.now();

    // Getters and setters
}
