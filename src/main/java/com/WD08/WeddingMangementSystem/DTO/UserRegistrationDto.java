package com.WD08.WeddingMangementSystem.DTO;


import lombok.Data;
import java.time.LocalDate;

@Data
public class UserRegistrationDto {
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
    private String role; // "ROLE_CUSTOMER" or "ROLE_ADMIN"

    // Customer Fields
    private LocalDate weddingDate;
    private Double budget;
    private Integer estimatedGuests;

    // Admin Fields
    private String department;
    private String accessLevel;
}
