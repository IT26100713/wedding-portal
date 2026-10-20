package model;

import java.util.ArrayList;

// Holds all the bookings so every category class works on the same list.
public class BookingList {
    private ArrayList<Booking> bookings;

    public BookingList() {
        bookings = new ArrayList<Booking>();
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public ArrayList<Booking> getBookings() {
        return bookings;
    }

    // Returns the booking with the given ID, or null if not found
    public Booking findBooking(int bookingId) {
        for (Booking b : bookings) {
            if (b.getBookingId() == bookingId) {
                return b;
            }
        }
        return null;
    }
}
