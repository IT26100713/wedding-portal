package com.WD08.WeddingMangementSystem.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("ROLE_ADMIN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Admin extends User {

    @Column(name = "department")
    private String department;

    @Column(name = "access_level")
    private String accessLevel;
}
