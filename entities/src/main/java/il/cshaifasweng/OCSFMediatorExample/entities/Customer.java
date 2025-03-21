package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;

@Entity
public class Customer extends Person {

    @Column(nullable = false)
    private String creditCardNumber;

    @Column(nullable = false)
    private String expirationDate;

    @Column(nullable = false)
    private String cvv;

    @Column(nullable = false)
    private String phoneNumber;

    // Default constructor
    public Customer() {}

    // Constructor with fields
    public Customer(int id, String name, String address, String email, String creditCardNumber, String expirationDate, String cvv) {
        super(id, name, address, email);  // Call the Person constructor
        this.creditCardNumber = creditCardNumber;
        this.expirationDate = expirationDate;
        this.cvv = cvv;
    }
    // Constructor with fields
    public Customer(String name, String address, String email,String phoneNumber, String creditCardNumber, String expirationDate, String cvv) {
        super(name, address,phoneNumber,email);  // Call the Person constructor
        this.creditCardNumber = creditCardNumber;
        this.expirationDate = expirationDate;
        this.cvv = cvv;
    }
    // Constructor with fields
    public Customer(String name,String email,String phoneNumber, String creditCardNumber,String expirationDate, String cvv) {
        super(name,email,phoneNumber);  // Call the Person constructor
        this.creditCardNumber = creditCardNumber;
        this.expirationDate = expirationDate;
        this.cvv = cvv;
        this.phoneNumber=phoneNumber;
    }



    // Getters and Setters
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

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    @Override
    public String toString() {
        return "Customer{" +
                super.toString()+'\''+
                "creditCardNumber='" + creditCardNumber + '\'' +
                ", expirationDate='" + expirationDate + '\'' +
                ", cvv='" + cvv + '\'' +
                "} ";
    }

    public void setPhone(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getPhone() {
        return phoneNumber;
    }
}
