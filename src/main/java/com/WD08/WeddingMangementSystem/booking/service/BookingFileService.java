package com.WD08.WeddingMangementSystem.booking.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.WD08.WeddingMangementSystem.booking.model.Booking;

// FILE HANDLING: keeps a copy of every booking in bookings.txt (in the project folder).
// MySQL is the main storage; this file is written again after every create, update
// and cancel, so it always matches the database, and it can be shown in the viva.
@Service
public class BookingFileService {
    private static final String FILE_NAME = "bookings.txt";

    // Writes all bookings to bookings.txt (the old content is replaced)
    public void writeAll(List<Booking> bookings, BookingService bookingService) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Booking b : bookings) {
                writer.println("========== Booking " + b.getId() + " ==========");
                writer.println("Bride        : " + b.getCouple().getBrideName());
                writer.println("Groom        : " + b.getCouple().getGroomName());
                writer.println("Phone        : " + b.getCouple().getPhone());
                writer.println("Photographer : " + orNotSelected(b.getPhotographer()));
                writer.println("Caterer      : " + orNotSelected(b.getCaterer()));
                writer.println("Decorator    : " + orNotSelected(b.getDecorator()));
                writer.println("Guests       : " + b.getGuestCount());
                writer.println("Event Date   : " + b.getEventDate());
                writer.println("Status       : " + b.getStatus());
                writer.println("Total (Rs.)  : " + bookingService.calculateTotal(b));
                writer.println();
            }
        } catch (IOException e) {
            System.out.println("Could not write " + FILE_NAME + ": " + e.getMessage());
        }
    }

    // Reads bookings.txt line by line, or returns an empty list if it does not exist yet
    public List<String> readAll() {
        List<String> lines = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            // No file yet: nothing has been booked
        }
        return lines;
    }

    private String orNotSelected(String vendorName) {
        if (vendorName == null) {
            return "Not selected";
        }
        return vendorName;
    }
}
