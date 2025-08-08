package com.example.unitedservice.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MissionRequestDTO {
    private Long id;
    private Long workerId;
    private String destination;
    private LocalDate missionDate;
    private String status;
    private DecisionDTO secretaireDecision;
    private DecisionDTO rhDecision;
    private DecisionDTO adminDecision;
    private LocalDate dateRequest;
}
