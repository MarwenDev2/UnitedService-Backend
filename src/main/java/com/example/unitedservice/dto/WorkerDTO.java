package com.example.unitedservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkerDTO {
    private long id;
    private String name;
    private String cin;
    private String department;
    private String position;
    private String phone;
    private String email;
    private float salary;
    private String profileImagePath;
    private String gender;
    private LocalDate dateOfBirth;
    private String address;
    private LocalDate creationDate;
    private String status;
    private int totalCongeDays;
    private int usedCongeDays;
}