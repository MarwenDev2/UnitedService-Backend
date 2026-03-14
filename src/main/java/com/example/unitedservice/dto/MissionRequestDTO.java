package com.example.unitedservice.dto;

import com.example.unitedservice.entities.Worker;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MissionRequestDTO {
    private Long id;
    private List<Worker> workers;
    private String destination;
    private LocalDate missionDate;
    private LocalDate endDate;
    private String status;
    private DecisionDTO secretaireDecision;
    private DecisionDTO rhDecision;
    private DecisionDTO adminDecision;
    private LocalDate dateRequest;
}
