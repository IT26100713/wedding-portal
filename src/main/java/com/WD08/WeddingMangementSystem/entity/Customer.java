package com.WD08.WeddingMangementSystem.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("ROLE_CUSTOMER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Customer extends User {

    @Column(name = "wedding_date")
    private LocalDate weddingDate;

    @Column(name = "budget")
    private Double budget;

    @Column(name = "estimated_guests")
    private Integer estimatedGuests;
}
