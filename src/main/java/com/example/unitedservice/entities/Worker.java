package com.example.unitedservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @JsonIgnore
    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL)
    private List<DemandeConge> demandes;

}
