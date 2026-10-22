package com.WD08.WeddingMangementSystem.booking.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

// A Booking links one Couple to up to 3 Vendors:
// a photographer, a caterer and a decorator.
// @Entity: each Booking object is saved as one row of the MySQL table "bookings".
// The vendor columns hold the vendor's name, or NULL if that service was not chosen.
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // MySQL AUTO_INCREMENT
    private Integer id;

    @Valid
    @Embedded
    private Couple couple;

    @Column(length = 100)
    private String photographer;

    @Column(length = 100)
    private String caterer;

    @Column(length = 100)
    private String decorator;

    @NotNull(message = "Event date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)   // reads "2026-12-20" from the form
    @Column(nullable = false)
    private LocalDate eventDate;

    @Column(length = 20, nullable = false)
    private String status;   // "Confirmed" or "Cancelled"

    // Default constructor
    public Booking() {
        couple = new Couple();
        status = "Confirmed";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Couple getCouple() {
        return couple;
    }

    public void setCouple(Couple couple) {
        this.couple = couple;
    }

    public String getPhotographer() {
        return photographer;
    }

    public void setPhotographer(String photographer) {
        this.photographer = photographer;
    }

    public String getCaterer() {
        return caterer;
    }

    public void setCaterer(String caterer) {
        this.caterer = caterer;
    }

    public String getDecorator() {
        return decorator;
    }

    public void setDecorator(String decorator) {
        this.decorator = decorator;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCancelled() {
        return "Cancelled".equals(status);
    }
}
