package view;

import model.Booking;
import model.BookingList;

public class ViewBooking {
    private BookingList bookingList;

    public ViewBooking(BookingList bookingList) {
        this.bookingList = bookingList;
    }

    // View all bookings
    public void viewBooking() {
        if (bookingList.getBookings().size() == 0) {
            System.out.println("No bookings found.");
            return;
        }
        for (Booking b : bookingList.getBookings()) {
            b.displayDetails();
        }
    }

    // View one booking by ID (method overloading: same name, different parameters)
    public void viewBooking(int bookingId) {
        Booking booking = bookingList.findBooking(bookingId);
        if (booking == null) {
            System.out.println("Booking " + bookingId + " not found.");
        } else {
            booking.displayDetails();
        }
    }
}
