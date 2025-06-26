package com.cars.child_abuse_reporting_system.entities;

import com.cars.child_abuse_reporting_system.enums.NoteType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "summariesS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Summary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    @JsonIgnore
    private CaseReport relatedCase;



    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "incident_details", columnDefinition = "TEXT")
    private String incidentDetails;

    @Column(name = "initial_assessment", columnDefinition = "TEXT")
    private String initialAssessment;

    @Column(name = "current_updates", columnDefinition = "TEXT")
    private String currentUpdates;

    @Enumerated(EnumType.STRING)
    @Column(name = "note_type", nullable = false)
    private NoteType noteType;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
