package com.example.unitedservice.dto;

import com.example.unitedservice.entities.Status;
import com.example.unitedservice.entities.TypeConge;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemandeCongeDTO {
    private Long id;
    private WorkerDTO worker;
    private TypeConge type;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private Status status;
    private DecisionDTO secretaireDecision;
    private DecisionDTO rhDecision;
    private DecisionDTO adminDecision;
    private String attachmentPath;
    private LocalDate dateDemande;
}