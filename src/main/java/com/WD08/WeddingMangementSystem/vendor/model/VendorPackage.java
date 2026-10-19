package com.WD08.WeddingMangementSystem.vendor.model;

public class VendorPackage {

    private Long id;
    private Long vendorId;
    private String name;
    private String description;
    private double price;

    public VendorPackage() {
    }

    // >>> Alt+Insert -> Getter and Setter -> select ALL fields <<<
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

}
