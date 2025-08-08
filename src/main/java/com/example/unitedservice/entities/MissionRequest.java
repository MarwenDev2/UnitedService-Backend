package com.example.unitedservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "mission_requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MissionRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private LocalDate missionDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rh_decision_id", nullable = true)
    private Decision rhDecision;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "secretaire_decision_id", nullable = true)
    private Decision secretaireDecision;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "admin_decision_id", nullable = true)
    private Decision adminDecision;

    @Column(nullable = false)
    private LocalDate dateRequest = LocalDate.now();
}