package create;

import model.Booking;
import model.BookingList;
import model.Couple;
import model.Vendor;

public class CreateBooking {
    private BookingList bookingList;
    private int nextBookingId;

    public CreateBooking(BookingList bookingList) {
        this.bookingList = bookingList;
        this.nextBookingId = 1;
    }

    // Creates a new booking that links a couple to the chosen vendors.
    // Pass null for any service the couple does not want.
    public Booking createBooking(Couple couple, Vendor photographer, Vendor caterer,
                                 Vendor decorator, String eventDate) {
        if (photographer == null && caterer == null && decorator == null) {
            System.out.println("Please choose at least one vendor.");
            return null;
        }
        Booking booking = new Booking(nextBookingId, couple, photographer,
                caterer, decorator, eventDate);
        bookingList.addBooking(booking);
        nextBookingId++;
        System.out.println("Booking created successfully. Booking ID: " + booking.getBookingId());
        System.out.println("Total price: Rs." + booking.calculateTotal());
        return booking;
    }
}
