package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

import il.cshaifasweng.OCSFMediatorExample.entities.Customer;
import il.cshaifasweng.OCSFMediatorExample.entities.Delivery;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchToCCInfoDelivery;
import static il.cshaifasweng.OCSFMediatorExample.client.App.switchToDelivery;

public class PersonalDetailsFillingDeliveryBoundary {
    public SimpleClient client;
    public Label errorLabel;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backBtn;

    @FXML
    private Button contToCCInfoBtn;

    @FXML
    private TextField mailTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField phoneTextField;

    @FXML
    private TextField addressTextField;

    @FXML
    private ComboBox<String> hoursList;


    private Delivery currentDelivery = null;

    public void setDelivery(Delivery delivery) {
        this.currentDelivery = delivery;
        System.out.println("delivery passed to PD");
    }

    public PersonalDetailsFillingDeliveryBoundary() {
    }


    @FXML
    void contToCCinfoFill(ActionEvent event) {
        String name = nameTextField.getText();
        String phone = phoneTextField.getText();
        String mail = mailTextField.getText();
        String address = addressTextField.getText();
        String time = hoursList.getSelectionModel().getSelectedItem();
        if (name.isEmpty() || phone.isEmpty() || mail.isEmpty() || address.isEmpty()) {
            errorLabel.setText("Please enter all the fields.");
        } else if (!isValidPhone(phone)) {
            errorLabel.setText("Please enter a valid phone number.");
        } else if (!isValidEmail(mail)) {
            errorLabel.setText("Please enter a valid email address.");
        } else {
            // Create a Customer object with partial information (credit card details will be added later)
            Customer customer = new Customer(name, address, mail, phone, null, null, null);

            if (currentDelivery != null) {
                currentDelivery.setCustomer(customer);
                currentDelivery.setDeliveryTime(time);
                System.out.println("Customer added to delivery: " + customer+ "delivery time "+ time);
            } else {
                System.out.println("Delivery object is null, cannot assign customer.");
            }

            switchToCCInfoDelivery(currentDelivery);
        }
    }

    @FXML
    void backToDelivery(ActionEvent event) {
        // Reset the orderItems list inside currentDelivery
        if (currentDelivery != null) {
            currentDelivery.getOrderItems().clear();  // Assuming `getOrderItems()` is the getter for the orderItems list
        }

        // Switch to the delivery page with the updated (reset) orderItems list
        switchToDelivery(currentDelivery);
    }

    @FXML
    void initialize() {
        assert backBtn != null : "fx:id=\"backBtn\" was not injected: check your FXML file 'personalDetailsFillingDelivery.fxml'.";
        assert contToCCInfoBtn != null : "fx:id=\"contToCCinfoBtn\" was not injected: check your FXML file 'personalDetailsFillingDelivery.fxml'.";
        assert mailTextField != null : "fx:id=\"mailTextField\" was not injected: check your FXML file 'personalDetailsFillingDelivery.fxml'.";
        assert nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'personalDetailsFillingDelivery.fxml'.";
        assert phoneTextField != null : "fx:id=\"phoneTextField\" was not injected: check your FXML file 'personalDetailsFillingDelivery.fxml'.";
        assert addressTextField != null : "fx:id=\"addressTextField\" was not injected: check your FXML file 'personalDetailsFillingDelivery.fxml'.";
        assert hoursList != null : "fx:id=\"hoursList\" was not injected: check your FXML file 'personalDetailsFillingDelivery.fxml'.";
        setHoursList();

    }

    public static boolean isValidPhone(String phone) {
        return Pattern.compile("^(\\+\\d{1,3})?\\d{10,15}$").matcher(phone).matches();
    }

    public static boolean isValidEmail(String email) {
        return Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$").matcher(email).matches();
    }

    @FXML
    void chooseHour(ActionEvent event) {
        String chosen = hoursList.getSelectionModel().getSelectedItem();
        client = SimpleClient.getClient();
        client.mapReservation.put("Hours", chosen);
    }

    void setHoursList() {
        hoursList.getItems().add("10:00");
        hoursList.getItems().add("10:15");
        hoursList.getItems().add("10:30");
        hoursList.getItems().add("10:45");

        hoursList.getItems().add("11:00");
        hoursList.getItems().add("11:15");
        hoursList.getItems().add("11:30");
        hoursList.getItems().add("11:45");

        hoursList.getItems().add("12:00");
        hoursList.getItems().add("12:15");
        hoursList.getItems().add("12:30");
        hoursList.getItems().add("12:45");

        hoursList.getItems().add("13:00");
        hoursList.getItems().add("13:15");
        hoursList.getItems().add("13:30");
        hoursList.getItems().add("13:45");

        hoursList.getItems().add("14:00");
        hoursList.getItems().add("14:15");
        hoursList.getItems().add("14:30");
        hoursList.getItems().add("14:45");

        hoursList.getItems().add("15:00");

        hoursList.getItems().add("15:15");
        hoursList.getItems().add("15:30");
        hoursList.getItems().add("15:45");
        hoursList.getItems().add("16:00");
        hoursList.getItems().add("16:15");
        hoursList.getItems().add("16:30");
        hoursList.getItems().add("16:45");
        hoursList.getItems().add("17:00");
        hoursList.getItems().add("17:15");
        hoursList.getItems().add("17:30");
        hoursList.getItems().add("17:45");
        hoursList.getItems().add("18:00");
        hoursList.getItems().add("18:15");
        hoursList.getItems().add("18:30");
        hoursList.getItems().add("18:45");
        hoursList.getItems().add("19:00");
        hoursList.getItems().add("19:15");
        hoursList.getItems().add("19:30");
        hoursList.getItems().add("19:45");
        hoursList.getItems().add("20:00");
        hoursList.getItems().add("20:15");
        hoursList.getItems().add("20:30");

    }
}