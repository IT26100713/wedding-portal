package com.WD08.WeddingMangementSystem.booking.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.WD08.WeddingMangementSystem.booking.model.Booking;
import com.WD08.WeddingMangementSystem.booking.service.BookingService;
import com.WD08.WeddingMangementSystem.booking.service.VendorService;

import jakarta.validation.Valid;

// The booking web pages (Thymeleaf). Replaces the Scanner menu in Main.
//
//   GET  /bookings                  list of all bookings      (View)
//   GET  /bookings/new              empty form                (Create)
//   POST /bookings                  save the new booking
//   GET  /bookings/{id}/edit        form with the booking     (Update)
//   POST /bookings/{id}             save the changes
//   POST /bookings/{id}/cancel      delete it                 (Cancel)
@Controller
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final VendorService vendorService;

    public BookingController(BookingService bookingService, VendorService vendorService) {
        this.bookingService = bookingService;
        this.vendorService = vendorService;
    }

    // ---------- View ----------
    @GetMapping
    public String viewBookings(Model model) {
        List<Booking> bookings = bookingService.viewBooking();
        Map<Integer, Double> totals = new HashMap<Integer, Double>();
        for (Booking b : bookings) {
            totals.put(b.getId(), bookingService.calculateTotal(b));
        }
        model.addAttribute("bookings", bookings);
        model.addAttribute("totals", totals);
        return "booking/bookings";
    }

    // ---------- Create ----------
    @GetMapping("/new")
    public String newBookingForm(Model model) {
        return showForm(model, new Booking());
    }

    @PostMapping
    public String createBooking(@Valid @ModelAttribute("booking") Booking booking, BindingResult errors,
                                Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            return showForm(model, booking);
        }
        try {
            Booking saved = bookingService.createBooking(booking);
            redirect.addFlashAttribute("message", "Booking " + saved.getId()
                    + " created. Total: Rs." + bookingService.calculateTotal(saved));
            return "redirect:/bookings";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return showForm(model, booking);
        }
    }

    // ---------- Update ----------
    @GetMapping("/{id}/edit")
    public String editBookingForm(@PathVariable int id, Model model, RedirectAttributes redirect) {
        try {
            return showForm(model, bookingService.viewBooking(id));
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/bookings";
        }
    }

    @PostMapping("/{id}")
    public String updateBooking(@PathVariable int id, @Valid @ModelAttribute("booking") Booking booking,
                                BindingResult errors, Model model, RedirectAttributes redirect) {
        booking.setId(id);
        if (errors.hasErrors()) {
            return showForm(model, booking);
        }
        try {
            Booking saved = bookingService.updateBooking(id, booking);
            redirect.addFlashAttribute("message", "Booking " + id
                    + " updated. New total: Rs." + bookingService.calculateTotal(saved));
            return "redirect:/bookings";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return showForm(model, booking);
        }
    }

    // ---------- Cancel ----------
    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable int id, RedirectAttributes redirect) {
        try {
            bookingService.cancelBooking(id);
            redirect.addFlashAttribute("message", "Booking " + id + " cancelled and deleted.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/bookings";
    }

    // Puts the booking and the vendor lists on the form page
    private String showForm(Model model, Booking booking) {
        model.addAttribute("booking", booking);
        model.addAttribute("photographers", vendorService.getVendors("Photography"));
        model.addAttribute("caterers", vendorService.getVendors("Catering"));
        model.addAttribute("decorators", vendorService.getVendors("Decoration"));
        return "booking/booking-form";
    }
}
