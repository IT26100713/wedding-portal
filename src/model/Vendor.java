package model;

public class Vendor {
    private int vendorId;
    private String vendorName;
    private String serviceType;   // e.g. Photography, Catering, Decoration
    private double price;

    // Default constructor
    public Vendor() {
        vendorId = 0;
        vendorName = "";
        serviceType = "";
        price = 0.0;
    }

    // Parameterized constructor
    public Vendor(int vendorId, String vendorName, String serviceType, double price) {
        setVendorId(vendorId);
        this.vendorName = vendorName;
        this.serviceType = serviceType;
        setPrice(price);
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        if (vendorId > 0) {
            this.vendorId = vendorId;
        } else {
            System.out.println("Vendor ID must be greater than 0");
        }
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price > 0) {
            this.price = price;
        } else {
            System.out.println("Price must be greater than 0");
        }
    }

    public void displayDetails() {
        System.out.println("Vendor ID   : " + vendorId);
        System.out.println("Vendor      : " + vendorName + " (" + serviceType + ")");
        System.out.println("Price (Rs.) : " + price);
    }
}
