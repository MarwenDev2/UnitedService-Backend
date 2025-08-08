package com.example.unitedservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecisionDTO {
    private int id;
    private UserDTO decisionBy;
    private boolean approved;
    private String comment;
    private LocalDateTime date;
}