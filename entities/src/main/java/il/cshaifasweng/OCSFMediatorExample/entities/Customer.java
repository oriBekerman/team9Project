package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;

@Entity
public class Customer extends Person {

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String creditCardNumber;

    @Column(nullable = false)
    private String expirationDate;

    @Column(nullable = false)
    private String cvv;

    // Default constructor
    public Customer() {}

    // Constructor with fields
    public Customer(int id, String name, String address, String email, String phoneNumber, String creditCardNumber, String expirationDate, String cvv) {
        super(id, name, address, email);  // Call the Person constructor
        this.phoneNumber = phoneNumber;
        this.creditCardNumber = creditCardNumber;
        this.expirationDate = expirationDate;
        this.cvv = cvv;
    }

    // Getters and Setters

    public String getPhoneNumber() {return phoneNumber;}
    public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}

    public String getCreditCardNumber() {
        return creditCardNumber;
    }
    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }
    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCvv() {return cvv;}
    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "phoneNumber='" + phoneNumber + '\'' +
                "creditCardNumber='" + creditCardNumber + '\'' +
                ", expirationDate='" + expirationDate + '\'' +
                ", cvv='" + cvv + '\'' +
                "} " + super.toString();  // Inherit the toString from Person
    }
}
