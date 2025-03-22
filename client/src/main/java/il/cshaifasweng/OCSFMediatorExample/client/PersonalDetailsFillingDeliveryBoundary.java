package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchToCCInfoDelivery;
import static il.cshaifasweng.OCSFMediatorExample.client.App.switchToDelivery;

public class PersonalDetailsFillingDeliveryBoundary{
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

    private Delivery currentDelivery= null;

    public void setDelivery(Delivery delivery){
        this.currentDelivery= delivery;
        System.out.println("delivery passed to PD");
    }

    public PersonalDetailsFillingDeliveryBoundary() {}
    @FXML
    void contToCCinfoFill(ActionEvent event) {
        String name=nameTextField.getText();
        String phone=phoneTextField.getText();
        String mail=mailTextField.getText();
        if(name.isEmpty() || phone.isEmpty() || mail.isEmpty())
        {
            errorLabel.setText("Please enter all the fields.");
        }
        else if(!isValidPhone(phone))
        {
            errorLabel.setText("Please enter a valid phone number.");
        }
        else if (!isValidEmail(mail))
        {
            errorLabel.setText("Please enter a valid email address.");
        }
        else{
            // Create a Customer object with partial information (credit card details will be added later)
            Customer customer = new Customer(name, mail, phone, null, null, null);

            if (currentDelivery != null) {
                currentDelivery.setCustomer(customer);
                System.out.println("Customer added to delivery: " + customer);
            } else {
                System.out.println("Delivery object is null, cannot assign customer.");
            }

            switchToCCInfoDelivery(currentDelivery);
        }
    }

    @FXML
    void backToDelivery(ActionEvent event) {
        switchToDelivery(currentDelivery);
    }
    @FXML
    void initialize() {
        assert backBtn != null : "fx:id=\"backBtn\" was not injected: check your FXML file 'personalDetailsFillingDelivery.fxml'.";
        assert contToCCInfoBtn != null : "fx:id=\"contToCCinfoBtn\" was not injected: check your FXML file 'personalDetailsFillingDelivery.fxml'.";
        assert mailTextField != null : "fx:id=\"mailTextField\" was not injected: check your FXML file 'personalDetailsFillingDelivery.fxml'.";
        assert nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'personalDetailsFillingDelivery.fxml'.";
        assert phoneTextField != null : "fx:id=\"phoneTextField\" was not injected: check your FXML file 'personalDetailsFillingDelivery.fxml'.";


    }
    public static boolean isValidPhone(String phone) {
        return Pattern.compile("^(\\+\\d{1,3})?\\d{10,15}$").matcher(phone).matches();
    }

    public static boolean isValidEmail(String email) {
        return Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$").matcher(email).matches();
    }

}