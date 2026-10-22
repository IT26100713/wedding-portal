package com.WD08.WeddingMangementSystem.booking.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.WD08.WeddingMangementSystem.booking.model.Booking;
import com.WD08.WeddingMangementSystem.booking.repository.BookingRepository;

// The booking rules: create, view, update and cancel.
// (These were the CreateBooking, ViewBooking, UpdateBooking and CancelBooking classes.)
// A rule that is broken throws IllegalArgumentException with a message for the page.
@Service
public class BookingService {
    private final BookingRepository repository;
    private final VendorService vendorService;

    // Spring passes in the repository and vendor service (dependency injection)
    public BookingService(BookingRepository repository, VendorService vendorService) {
        this.repository = repository;
        this.vendorService = vendorService;
    }

    // ---------- Create ----------
    // Saves a new booking. Leave a vendor empty for a service that is not needed.
    public Booking createBooking(Booking booking) {
        booking.setId(null);   // MySQL gives the ID
        booking.setStatus("Confirmed");
        checkVendors(booking);
        return repository.save(booking);
    }

    // ---------- View ----------
    public List<Booking> viewBooking() {
        return repository.findAll();
    }

    // View one booking by ID (method overloading: same name, different parameters)
    public Booking viewBooking(int bookingId) {
        return repository.findById(bookingId).orElseThrow(() ->
                new IllegalArgumentException("Booking " + bookingId + " not found."));
    }

    // ---------- Update ----------
    // Changes the event date and vendors of a booking (the couple stays the same)
    public Booking updateBooking(int bookingId, Booking changes) {
        Booking booking = viewBooking(bookingId);
        if (booking.isCancelled()) {
            throw new IllegalArgumentException("Booking " + bookingId + " is cancelled and cannot be updated.");
        }
        booking.setPhotographer(changes.getPhotographer());
        booking.setCaterer(changes.getCaterer());
        booking.setDecorator(changes.getDecorator());
        booking.setEventDate(changes.getEventDate());
        checkVendors(booking);
        return repository.save(booking);
    }

    // ---------- Cancel ----------
    // Cancels a booking by deleting its row from the bookings table
    public void cancelBooking(int bookingId) {
        Booking booking = viewBooking(bookingId);
        repository.delete(booking);
    }

    // Adds up the price of every vendor that was chosen
    public double calculateTotal(Booking b) {
        return vendorService.priceOf(b.getPhotographer(), "Photography")
                + vendorService.priceOf(b.getCaterer(), "Catering")
                + vendorService.priceOf(b.getDecorator(), "Decoration");
    }

    // At least one vendor must be chosen, and each name must be a real vendor
    private void checkVendors(Booking b) {
        b.setPhotographer(emptyToNull(b.getPhotographer()));
        b.setCaterer(emptyToNull(b.getCaterer()));
        b.setDecorator(emptyToNull(b.getDecorator()));

        if (b.getPhotographer() == null && b.getCaterer() == null && b.getDecorator() == null) {
            throw new IllegalArgumentException("Please choose at least one vendor.");
        }
        checkVendor(b.getPhotographer(), "Photography");
        checkVendor(b.getCaterer(), "Catering");
        checkVendor(b.getDecorator(), "Decoration");
    }

    private void checkVendor(String name, String serviceType) {
        if (name != null && vendorService.findVendor(name, serviceType) == null) {
            throw new IllegalArgumentException("Unknown " + serviceType + " vendor: " + name);
        }
    }

    private String emptyToNull(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return s;
    }
}
