package com.WD08.WeddingMangementSystem.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.WD08.WeddingMangementSystem.booking.model.Booking;

// Spring writes the SQL for us: save(), findAll(), findById() ...
// <Booking, Integer> means: it stores Booking objects and the ID is an Integer.
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    // Spring makes the SQL from the method name:
    // SELECT * FROM bookings WHERE phone = ? ORDER BY event_date
    List<Booking> findByCouplePhoneOrderByEventDate(String phone);
}
