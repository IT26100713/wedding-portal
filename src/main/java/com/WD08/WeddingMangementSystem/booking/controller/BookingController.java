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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.WD08.WeddingMangementSystem.booking.model.Booking;
import com.WD08.WeddingMangementSystem.booking.service.BookingService;
import com.WD08.WeddingMangementSystem.booking.service.VendorService;

import jakarta.validation.Valid;

// The booking web pages (Thymeleaf). Couples make and manage their own bookings.
//
//   GET  /bookings/new                       booking form for a couple   (Create)
//   POST /bookings                           save the new booking
//   GET  /bookings/my?phone=...              the couple's bookings        (Read)
//   GET  /bookings/{id}/edit?phone=...       form with their booking      (Update)
//   POST /bookings/{id}?phone=...            save the changes
//   POST /bookings/{id}/cancel?phone=...     delete their booking         (Delete)
@Controller
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final VendorService vendorService;

    public BookingController(BookingService bookingService, VendorService vendorService) {
        this.bookingService = bookingService;
        this.vendorService = vendorService;
    }

    // /bookings opens the couple's booking form
    @GetMapping
    public String home() {
        return "redirect:/bookings/new";
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
            redirect.addFlashAttribute("message", "Thank you! Your booking (ID " + saved.getId()
                    + ") is confirmed. Total: Rs." + bookingService.calculateTotal(saved));
            redirect.addAttribute("phone", saved.getCouple().getPhone());
            return "redirect:/bookings/my";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return showForm(model, booking);
        } catch (RuntimeException e) {
            // e.g. MySQL is not running: show the reason on the page
            System.out.println("Booking could not be saved: " + e);
            model.addAttribute("error", "The booking could not be saved: " + e.getMessage());
            return showForm(model, booking);
        }
    }

    // ---------- Read ----------
    @GetMapping("/my")
    public String myBookings(@RequestParam(required = false) String phone, Model model) {
        model.addAttribute("phone", phone);
        if (phone != null && !phone.isBlank()) {
            List<Booking> bookings = bookingService.viewBooking(phone);
            model.addAttribute("bookings", bookings);
            model.addAttribute("totals", totalsOf(bookings));
        }
        return "booking/my-bookings";
    }

    // ---------- Update ----------
    @GetMapping("/{id}/edit")
    public String editBookingForm(@PathVariable int id, @RequestParam String phone,
                                  Model model, RedirectAttributes redirect) {
        try {
            return showForm(model, bookingService.viewBooking(id, phone));
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addAttribute("phone", phone);
            return "redirect:/bookings/my";
        }
    }

    @PostMapping("/{id}")
    public String updateBooking(@PathVariable int id, @RequestParam String phone,
                                @Valid @ModelAttribute("booking") Booking booking, BindingResult errors,
                                Model model, RedirectAttributes redirect) {
        booking.setId(id);
        if (errors.hasErrors()) {
            return showForm(model, booking);
        }
        try {
            Booking saved = bookingService.updateBooking(id, phone, booking);
            redirect.addFlashAttribute("message", "Booking " + id
                    + " updated. New total: Rs." + bookingService.calculateTotal(saved));
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return showForm(model, booking);
        } catch (RuntimeException e) {
            // e.g. MySQL is not running: show the reason on the page
            System.out.println("Booking could not be saved: " + e);
            model.addAttribute("error", "The booking could not be saved: " + e.getMessage());
            return showForm(model, booking);
        }
        redirect.addAttribute("phone", phone);
        return "redirect:/bookings/my";
    }

    // ---------- Delete ----------
    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable int id, @RequestParam String phone,
                                RedirectAttributes redirect) {
        try {
            bookingService.cancelBooking(id, phone);
            redirect.addFlashAttribute("message", "Booking " + id + " cancelled and deleted.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        redirect.addAttribute("phone", phone);
        return "redirect:/bookings/my";
    }

    // Total price of each booking, by booking ID
    private Map<Integer, Double> totalsOf(List<Booking> bookings) {
        Map<Integer, Double> totals = new HashMap<Integer, Double>();
        for (Booking b : bookings) {
            totals.put(b.getId(), bookingService.calculateTotal(b));
        }
        return totals;
    }

    // Puts the booking and the vendor lists on the form page
    private String showForm(Model model, Booking booking) {
        model.addAttribute("booking", booking);
        model.addAttribute("photographers", vendorService.getVendors("Photography"));
        model.addAttribute("caterers", vendorService.getVendors("Catering"));
        model.addAttribute("decorators", vendorService.getVendors("Decoration"));
        model.addAttribute("venues", vendorService.getVendors("Venue"));
        return "booking/booking-form";
    }
}
