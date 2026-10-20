package model;

// A Booking links one Couple to up to 3 Vendors:
// a photographer, a caterer and a decorator.
// A vendor that was not chosen is null, so any combination is possible.
// The Couple and Vendors are created outside and passed in (Aggregation).
public class Booking {
    private int bookingId;
    private Couple couple;
    private Vendor photographer;
    private Vendor caterer;
    private Vendor decorator;
    private String eventDate;
    private String status;   // "Confirmed" or "Cancelled"

    // Default constructor
    public Booking() {
        bookingId = 0;
        couple = null;
        photographer = null;
        caterer = null;
        decorator = null;
        eventDate = "";
        status = "Confirmed";
    }

    // Parameterized constructor (pass null for a service that is not needed)
    public Booking(int bookingId, Couple couple, Vendor photographer,
                   Vendor caterer, Vendor decorator, String eventDate) {
        setBookingId(bookingId);
        this.couple = couple;
        this.photographer = photographer;
        this.caterer = caterer;
        this.decorator = decorator;
        this.eventDate = eventDate;
        this.status = "Confirmed";
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        if (bookingId > 0) {
            this.bookingId = bookingId;
        } else {
            System.out.println("Booking ID must be greater than 0");
        }
    }

    public Couple getCouple() {
        return couple;
    }

    public void setCouple(Couple couple) {
        this.couple = couple;
    }

    public Vendor getPhotographer() {
        return photographer;
    }

    public void setPhotographer(Vendor photographer) {
        this.photographer = photographer;
    }

    public Vendor getCaterer() {
        return caterer;
    }

    public void setCaterer(Vendor caterer) {
        this.caterer = caterer;
    }

    public Vendor getDecorator() {
        return decorator;
    }

    public void setDecorator(Vendor decorator) {
        this.decorator = decorator;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Adds up the price of every vendor that was chosen
    public double calculateTotal() {
        double total = 0;
        if (photographer != null) {
            total = total + photographer.getPrice();
        }
        if (caterer != null) {
            total = total + caterer.getPrice();
        }
        if (decorator != null) {
            total = total + decorator.getPrice();
        }
        return total;
    }

    // Prints one vendor line, or "Not selected"
    private void displayVendor(String label, Vendor vendor) {
        if (vendor == null) {
            System.out.println(label + ": Not selected");
        } else {
            System.out.println(label + ": " + vendor.getVendorName()
                    + " (Rs." + vendor.getPrice() + ")");
        }
    }

    public void displayDetails() {
        System.out.println("----------------------------------");
        System.out.println("Booking ID  : " + bookingId);
        System.out.println("Event Date  : " + eventDate);
        System.out.println("Status      : " + status);
        couple.displayDetails();
        displayVendor("Photographer", photographer);
        displayVendor("Caterer     ", caterer);
        displayVendor("Decorator   ", decorator);
        System.out.println("Total (Rs.) : " + calculateTotal());
        System.out.println("----------------------------------");
    }
}
