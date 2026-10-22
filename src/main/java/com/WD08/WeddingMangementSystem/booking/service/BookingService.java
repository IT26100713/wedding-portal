package com.WD08.WeddingMangementSystem.booking.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.WD08.WeddingMangementSystem.booking.model.Booking;
import com.WD08.WeddingMangementSystem.booking.repository.BookingRepository;

// The booking rules: create, view, update and cancel.
// Couples make and manage their own bookings. A couple is recognised by their
// phone number, so they can only see, change or cancel bookings with that number.
// A rule that is broken throws IllegalArgumentException with a message for the page.
@Service
public class BookingService {
    private final BookingRepository repository;
    private final VendorService vendorService;
    private final BookingFileService fileService;

    // Spring passes in these objects (dependency injection)
    public BookingService(BookingRepository repository, VendorService vendorService,
                          BookingFileService fileService) {
        this.repository = repository;
        this.vendorService = vendorService;
        this.fileService = fileService;
    }

    // ---------- Create ----------
    // A couple books a wedding. Leave a vendor empty for a service that is not needed.
    public Booking createBooking(Booking booking) {
        booking.setId(null);   // MySQL gives the ID
        booking.setStatus("Confirmed");
        checkVendors(booking);
        Booking saved = repository.save(booking);
        saveToFile();
        return saved;
    }

    // ---------- Read ----------
    // All bookings (admin view)
    public List<Booking> viewBooking() {
        return repository.findAll();
    }

    // The bookings of one couple, found by their phone number
    public List<Booking> viewBooking(String phone) {
        return repository.findByCouplePhoneOrderByEventDate(phone.trim());
    }

    // One booking by ID (method overloading: same name, different parameters)
    public Booking viewBooking(int bookingId) {
        return repository.findById(bookingId).orElseThrow(() ->
                new IllegalArgumentException("Booking " + bookingId + " not found."));
    }

    // One booking, only if it belongs to the couple with this phone number
    public Booking viewBooking(int bookingId, String phone) {
        Booking booking = viewBooking(bookingId);
        if (phone == null || !booking.getCouple().getPhone().equals(phone.trim())) {
            throw new IllegalArgumentException("Booking " + bookingId + " does not belong to this phone number.");
        }
        return booking;
    }

    // ---------- Update ----------
    // The couple changes the vendors, guest count or date of their own booking
    public Booking updateBooking(int bookingId, String phone, Booking changes) {
        Booking booking = viewBooking(bookingId, phone);
        booking.setPhotographer(changes.getPhotographer());
        booking.setCaterer(changes.getCaterer());
        booking.setDecorator(changes.getDecorator());
        booking.setGuestCount(changes.getGuestCount());
        booking.setEventDate(changes.getEventDate());
        checkVendors(booking);
        Booking saved = repository.save(booking);
        saveToFile();
        return saved;
    }

    // ---------- Delete ----------
    // The couple cancels their own booking: it is deleted from the bookings table
    public void cancelBooking(int bookingId, String phone) {
        Booking booking = viewBooking(bookingId, phone);
        repository.delete(booking);
        saveToFile();
    }

    // Adds up the cost of every vendor that was chosen.
    // Each vendor works out its own cost (Photographer, Caterer and Decorator differ).
    public double calculateTotal(Booking b) {
        int guests = b.getGuestCount() == null ? 0 : b.getGuestCount();
        return vendorService.costOf(b.getPhotographer(), "Photography", guests)
                + vendorService.costOf(b.getCaterer(), "Catering", guests)
                + vendorService.costOf(b.getDecorator(), "Decoration", guests);
    }

    // Lines of the bookings.txt file, for the admin page
    public List<String> readBookingFile() {
        return fileService.readAll();
    }

    // Writes every booking to bookings.txt so the file matches the database
    private void saveToFile() {
        fileService.writeAll(repository.findAll(), this);
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
