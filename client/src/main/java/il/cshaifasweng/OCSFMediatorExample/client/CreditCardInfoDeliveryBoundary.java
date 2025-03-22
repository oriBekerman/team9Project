package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import il.cshaifasweng.OCSFMediatorExample.client.Events.CreditCardInfoSet;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;

import static il.cshaifasweng.OCSFMediatorExample.client.App.*;

public class CreditCardInfoDeliveryBoundary {

    @FXML
    public TextField cardNumText;
    @FXML
    public TextField expDateText;
    @FXML
    public TextField cvvText;
    @FXML
    public Label errorLabel;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backBtn;

    @FXML
    private Button paymentBtn;
    private Delivery currentDelivery= null;

    public void setDelivery(Delivery delivery){
        this.currentDelivery= delivery;
        System.out.println("delivery passed to credit card info");
    }

    public CreditCardInfoDeliveryBoundary() {}

    @FXML
    void backToPersonalD(ActionEvent event) {
        switchToPDDelivery(currentDelivery);
    }

    @FXML
    void checkPayment(ActionEvent event) throws IOException {
        String cardNum = cardNumText.getText();
        String expDate = expDateText.getText();
        String cvv = cvvText.getText();
        try {
            if (cardNum.isEmpty() || expDate.isEmpty() || cvv.isEmpty()) {
                errorLabel.setText("Please fill all the fields");
            } else if (!isValidCreditCard(cardNum)) {
                errorLabel.setText("Invalid Card Number");
            } else if (!isValidExpDate(expDate)) {
                errorLabel.setText("Invalid Expiry Date");
            } else if (!isValidCVV(cvv)) {
                errorLabel.setText("Invalid CVV");
            } else {
                if (currentDelivery != null) {
                    // add credit card to custumer
                    Customer customer = currentDelivery.getCustomer();
                    if(customer!=null) {
                        customer.setCreditCardNumber(cardNum);
                        customer.setCvv(cvv);
                        customer.setExpirationDate(expDate);
                        currentDelivery.setCustomer(customer);
                        // Set the current date and time for the delivery
                        LocalDateTime now = LocalDateTime.now();
                        currentDelivery.setDeliveryTime(now);

                        // Create the request to send to the server for delivery creation
                        Request<Delivery> createDeliveryRequest = new Request<>(
                                ReqCategory.DELIVERY,
                                RequestType.CREATE_DELIVERY,
                                currentDelivery
                        );

                        // Assuming you have a method for sending requests to the server
                        SimpleClient.getClient().sendToServer(createDeliveryRequest);
                        System.out.println(currentDelivery);
                    }
                    else{
                        System.out.println("custumer is null");
                    }
                } else {
                    System.out.println("Delivery object is null, cannot assign customer.");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();  // Log the error
            errorLabel.setText("An unexpected error occurred.");
        }
    }

    @FXML
    void initialize() {
        assert backBtn != null : "fx:id=\"backBtn\" was not injected: check your FXML file 'creditCardInfo.fxml'.";
        assert paymentBtn != null : "fx:id=\"paymentBtn\" was not injected: check your FXML file 'creditCardInfo.fxml'.";
        assert cardNumText != null : "fx:id=\"cardNumText\" was not injected: check your FXML file 'creditCardInfo.fxml'.";
        assert expDateText != null : "fx:id=\"expDateText\" was not injected: check your FXML file 'creditCardInfo.fxml'.";
        assert cvvText != null : "fx:id=\"cvvText\" was not injected: check your FXML file 'creditCardInfo.fxml'.";
        assert errorLabel != null : "fx:id=\"errorLabel\" was not injected: check your FXML file 'creditCardInfo.fxml'.";
    }


    //validate card number

    public boolean isValidCreditCard(String cardNumber) {
        if (!Pattern.compile("^[0-9]{13,19}$").matcher(cardNumber).matches()) {
            return false;
        }
        return luhnCheck(cardNumber);
    }
    //used to validate card number
    private boolean luhnCheck(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }
    public static boolean isValidCVV(String cvv) {
        return Pattern.compile("^[0-9]{3,4}$").matcher(cvv).matches();
    }

    //check expiration date is a valid date
    public static boolean isValidExpDate(String expDate) {
        if (!Pattern.compile("^(0[1-9]|1[0-2])/(\\d{2}|\\d{4})$").matcher(expDate).matches()) {
            return false;
        }
        return isNotExpired(expDate);
    }
    //check exp date has not passed
    private static boolean isNotExpired(String expDate) {
        try {
            DateTimeFormatter formatter = expDate.length() == 5 ? DateTimeFormatter.ofPattern("MM/yy")
                    : DateTimeFormatter.ofPattern("MM/yyyy");
            YearMonth cardExpiry = YearMonth.parse(expDate, formatter);
            return cardExpiry.isAfter(YearMonth.now()); // Expiration must be in the future
        } catch (
                DateTimeParseException e) {
            System.out.println("Invalid date format: " + expDate);
            return false;
        }
    }


}
