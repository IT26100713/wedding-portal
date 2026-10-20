package model;

public class Couple {
    private int coupleId;
    private String brideName;
    private String groomName;
    private String phone;

    // Default constructor
    public Couple() {
        coupleId = 0;
        brideName = "";
        groomName = "";
        phone = "";
    }

    // Parameterized constructor
    public Couple(int coupleId, String brideName, String groomName, String phone) {
        setCoupleId(coupleId);
        this.brideName = brideName;
        this.groomName = groomName;
        this.phone = phone;
    }

    public int getCoupleId() {
        return coupleId;
    }

    public void setCoupleId(int coupleId) {
        if (coupleId > 0) {
            this.coupleId = coupleId;
        } else {
            System.out.println("Couple ID must be greater than 0");
        }
    }

    public String getBrideName() {
        return brideName;
    }

    public void setBrideName(String brideName) {
        this.brideName = brideName;
    }

    public String getGroomName() {
        return groomName;
    }

    public void setGroomName(String groomName) {
        this.groomName = groomName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void displayDetails() {
        System.out.println("Couple ID   : " + coupleId);
        System.out.println("Couple      : " + brideName + " & " + groomName);
        System.out.println("Phone       : " + phone);
    }
}
