package com.WD08.WeddingMangementSystem.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

// The couple who makes a booking.
// @Embeddable: the couple's details are saved as columns of the bookings table
// (bride_name, groom_name, phone), inside each Booking row.
@Embeddable
public class Couple {

    @NotBlank(message = "Bride name is required")
    @Column(length = 100, nullable = false)
    private String brideName;

    @NotBlank(message = "Groom name is required")
    @Column(length = 100, nullable = false)
    private String groomName;

    @NotBlank(message = "Phone number is required")
    @Column(length = 20, nullable = false)
    private String phone;

    // Default constructor (JPA needs it)
    public Couple() {
        brideName = "";
        groomName = "";
        phone = "";
    }

    // Parameterized constructor
    public Couple(String brideName, String groomName, String phone) {
        this.brideName = brideName;
        this.groomName = groomName;
        this.phone = phone;
    }

    public String getBrideName() {
        return brideName;
    }

    public void setBrideName(String brideName) {
        this.brideName = brideName;
    }

    public String getGroomName() {
        return groomName;
    }

    public void setGroomName(String groomName) {
        this.groomName = groomName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
