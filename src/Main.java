import java.util.Scanner;

import model.BookingList;
import model.Couple;
import model.Vendor;
import create.CreateBooking;
import view.ViewBooking;
import update.UpdateBooking;
import cancel.CancelBooking;

public class Main {

    // Shows a list of vendors and returns the one chosen, or null if the user enters 0
    public static Vendor chooseVendor(Scanner input, String title, Vendor[] vendors) {
        System.out.println(title + ":");
        for (int i = 0; i < vendors.length; i++) {
            System.out.println("  " + (i + 1) + ". " + vendors[i].getVendorName()
                    + " - Rs." + vendors[i].getPrice());
        }
        System.out.println("  0. Skip (not needed)");
        System.out.print("Your choice: ");
        int choice = input.nextInt();
        input.nextLine();

        if (choice >= 1 && choice <= vendors.length) {
            return vendors[choice - 1];
        }
        if (choice != 0) {
            System.out.println("Invalid choice, skipped.");
        }
        return null;
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // One shared list of bookings used by all 4 categories
        BookingList bookingList = new BookingList();
        CreateBooking create = new CreateBooking(bookingList);
        ViewBooking view = new ViewBooking(bookingList);
        UpdateBooking update = new UpdateBooking(bookingList);
        CancelBooking cancel = new CancelBooking(bookingList);

        // 3 vendors for each service (in the full system these come from Vendor Management)
        Vendor[] photographers = new Vendor[4];
        photographers[0] = new Vendor(1, "Lens Studio", "Photography", 75000);
        photographers[1] = new Vendor(2, "Golden Frame", "Photography", 90000);
        photographers[2] = new Vendor(3, "Snap Moments", "Photography", 60000);
        photographers[3] = new Vendor(4, "Helen's Studio", "Photography", 80000);


        Vendor[] caterers = new Vendor[4];
        caterers[0] = new Vendor(5, "Royal Caterers", "Catering", 250000);
        caterers[1] = new Vendor(6, "Spice Garden", "Catering", 200000);
        caterers[2] = new Vendor(7, "Taste of Lanka", "Catering", 180000);
        caterers[3] = new Vendor(8, "Hilton Caterers", "Catering", 300000);


        Vendor[] decorators = new Vendor[4];
        decorators[0] = new Vendor(9, "Bloom Decor", "Decoration", 120000);
        decorators[1] = new Vendor(10, "Elegant Events", "Decoration", 150000);
        decorators[2] = new Vendor(11, "Petal Touch", "Decoration", 100000);
        decorators[3] = new Vendor(12, "Charming Events", "Decoration", 180000);


        int coupleCount = 0;
        int choice = 0;

        while (choice != 5) {
            System.out.println();
            System.out.println("===== Booking Management =====");
            System.out.println("1. Create booking");
            System.out.println("2. View bookings");
            System.out.println("3. Update booking");
            System.out.println("4. Cancel booking");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            choice = input.nextInt();
            input.nextLine();

            if (choice == 1) {
                System.out.print("Bride's name: ");
                String bride = input.nextLine();
                System.out.print("Groom's name: ");
                String groom = input.nextLine();
                System.out.print("Phone number: ");
                String phone = input.nextLine();
                coupleCount++;
                Couple couple = new Couple(coupleCount, bride, groom, phone);

                // Choose any combination: enter 0 to skip a service
                Vendor photographer = chooseVendor(input, "Choose a photographer", photographers);
                Vendor caterer = chooseVendor(input, "Choose a caterer", caterers);
                Vendor decorator = chooseVendor(input, "Choose a decorator", decorators);

                System.out.print("Event date (YYYY-MM-DD): ");
                String date = input.nextLine();

                create.createBooking(couple, photographer, caterer, decorator, date);

            } else if (choice == 2) {
                System.out.print("Enter booking ID (0 to view all): ");
                int id = input.nextInt();
                input.nextLine();
                if (id == 0) {
                    view.viewBooking();
                } else {
                    view.viewBooking(id);
                }

            } else if (choice == 3) {
                System.out.print("Enter booking ID: ");
                int id = input.nextInt();
                input.nextLine();
                System.out.println("1. Change event date");
                System.out.println("2. Change photographer");
                System.out.println("3. Change caterer");
                System.out.println("4. Change decorator");
                System.out.print("Enter your choice: ");
                int option = input.nextInt();
                input.nextLine();

                if (option == 1) {
                    System.out.print("New event date (YYYY-MM-DD): ");
                    String date = input.nextLine();
                    update.updateBooking(id, date);
                } else if (option == 2) {
                    Vendor v = chooseVendor(input, "Choose a photographer (0 removes it)", photographers);
                    update.updateBooking(id, "Photography", v);
                } else if (option == 3) {
                    Vendor v = chooseVendor(input, "Choose a caterer (0 removes it)", caterers);
                    update.updateBooking(id, "Catering", v);
                } else if (option == 4) {
                    Vendor v = chooseVendor(input, "Choose a decorator (0 removes it)", decorators);
                    update.updateBooking(id, "Decoration", v);
                } else {
                    System.out.println("Invalid option.");
                }

            } else if (choice == 4) {
                System.out.print("Enter booking ID to cancel: ");
                int id = input.nextInt();
                input.nextLine();
                cancel.cancelBooking(id);

            } else if (choice == 5) {
                System.out.println("Goodbye!");

            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }

        input.close();
    }
}
