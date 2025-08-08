package com.example.unitedservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private int id;
    private WorkerDTO recipient;
    private String message;
    private LocalDateTime timestamp;
    private boolean read;
}