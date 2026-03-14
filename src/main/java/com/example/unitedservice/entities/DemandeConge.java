package com.example.unitedservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DemandeConge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker worker;

    @Enumerated(EnumType.STRING)
    private TypeConge type;

    private LocalDate startDate;
    private LocalDate endDate;

    private String reason;
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "secretaire_decision_id")
    private Decision secretaireDecision;

    @ManyToOne
    @JoinColumn(name = "rh_decision_id")
    private Decision rhDecision;

    @ManyToOne
    @JoinColumn(name = "admin_decision_id")
    private Decision adminDecision;

    private String attachmentPath;
    private LocalDate dateDemande;


}