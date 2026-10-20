package cancel;

import model.Booking;
import model.BookingList;

public class CancelBooking {
    private BookingList bookingList;

    public CancelBooking(BookingList bookingList) {
        this.bookingList = bookingList;
    }

    // Marks the booking as cancelled (it stays in the list as a record)
    public void cancelBooking(int bookingId) {
        Booking booking = bookingList.findBooking(bookingId);
        if (booking == null) {
            System.out.println("Booking " + bookingId + " not found.");
        } else if (booking.getStatus().equals("Cancelled")) {
            System.out.println("Booking " + bookingId + " is already cancelled.");
        } else {
            booking.setStatus("Cancelled");
            System.out.println("Booking " + bookingId + " cancelled successfully.");
        }
    }
}
