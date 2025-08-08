package com.example.unitedservice.dto;

import com.example.unitedservice.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private long id;
    private String name;
    private String email;
    private Role role;
    private String phone;
    private String profileImagePath;
    private String gender;
    private LocalDate dateOfBirth;
    private String address;
}