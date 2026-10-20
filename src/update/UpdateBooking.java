package update;

import model.Booking;
import model.BookingList;
import model.Vendor;

public class UpdateBooking {
    private BookingList bookingList;

    public UpdateBooking(BookingList bookingList) {
        this.bookingList = bookingList;
    }

    // Finds a booking that can be updated, or returns null
    private Booking findActiveBooking(int bookingId) {
        Booking booking = bookingList.findBooking(bookingId);
        if (booking == null) {
            System.out.println("Booking " + bookingId + " not found.");
            return null;
        }
        if (booking.getStatus().equals("Cancelled")) {
            System.out.println("Booking " + bookingId + " is cancelled and cannot be updated.");
            return null;
        }
        return booking;
    }

    // Change the event date of a booking
    public void updateBooking(int bookingId, String newEventDate) {
        Booking booking = findActiveBooking(bookingId);
        if (booking != null) {
            booking.setEventDate(newEventDate);
            System.out.println("Booking " + bookingId + " date updated to " + newEventDate);
        }
    }

    // Change one service of a booking (method overloading).
    // serviceType is "Photography", "Catering" or "Decoration".
    // Pass null as newVendor to remove that service.
    public void updateBooking(int bookingId, String serviceType, Vendor newVendor) {
        Booking booking = findActiveBooking(bookingId);
        if (booking == null) {
            return;
        }

        if (serviceType.equals("Photography")) {
            booking.setPhotographer(newVendor);
        } else if (serviceType.equals("Catering")) {
            booking.setCaterer(newVendor);
        } else if (serviceType.equals("Decoration")) {
            booking.setDecorator(newVendor);
        } else {
            System.out.println("Unknown service type.");
            return;
        }

        if (newVendor == null) {
            System.out.println(serviceType + " removed from booking " + bookingId);
        } else {
            System.out.println(serviceType + " for booking " + bookingId
                    + " changed to " + newVendor.getVendorName());
        }
        System.out.println("New total: Rs." + booking.calculateTotal());
    }
}
