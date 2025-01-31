package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "customers") // Specifies the table name for the Customer class in the database
public class Customer extends Person implements Serializable {

    @Embedded
    private CreditCardInfo creditCardInfo;

    // Default constructor
    public Customer() {}

    // Constructor with fields
    // Constructor with fields (for Person fields + creditCardInfo)
    public Customer(String name, String address, String email, CreditCardInfo creditCardInfo) {
        // Set fields from the Person class (inherited)
        this.setName(name);
        this.setAddress(address);
        this.setEmail(email);

        // Set creditCardInfo for the Customer
        this.creditCardInfo = creditCardInfo;
    }

    // Getters and Setters
    public CreditCardInfo getCreditCardInfo() {
        return creditCardInfo;
    }

    public void setCreditCardInfo(CreditCardInfo creditCardInfo) {
        this.creditCardInfo = creditCardInfo;
    }

    // CreditCardInfo structure
    @Embeddable
    public static class CreditCardInfo {

        @Column(nullable = false)
        private String cardNumber;

        @Column(nullable = false)
        private String expiryDate;

        // Default constructor
        public CreditCardInfo() {}

        // Constructor with fields
        public CreditCardInfo(String cardNumber, String expiryDate) {
            this.cardNumber = cardNumber;
            this.expiryDate = expiryDate;
        }

        // Getters and Setters
        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
        }

    }
}
