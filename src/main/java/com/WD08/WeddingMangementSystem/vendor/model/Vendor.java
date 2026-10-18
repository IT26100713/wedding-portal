package com.WD08.WeddingMangementSystem.vendor.model;

public abstract class Vendor {

    // ENCAPSULATION: fields are private, accessed only through getters/setters
    private Long id;
    private String businessName;
    private String ownerName;
    private String email;
    private String passwordHash;
    private String phone;
    private String city;
    private String description;
    private double baseRate;

    public Vendor() {
    }

    // >>> Alt+Insert -> Getter and Setter -> select ALL fields above <<

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getBaseRate() { return baseRate; }
    public void setBaseRate(double baseRate) { this.baseRate = baseRate; }

    // ABSTRACTION: each vendor type must define these itself
    public abstract String getCategory();

    public abstract String getPricingLabel();

    public abstract double calculateQuote(int units);


}
