package com.example.unitedservice.dto;
import lombok.Data;
import java.time.LocalDate;
@Data
public class DemandeAvanceDTO {
    private Long id;
    private Long workerId;
    private double requestedAmount;
    private double adminResponseAmount;
    private String status;
    private String adminComment;
    private LocalDate dateRequest;
}