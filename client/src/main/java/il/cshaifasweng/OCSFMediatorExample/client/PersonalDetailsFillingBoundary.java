package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

import il.cshaifasweng.OCSFMediatorExample.client.Events.ComplaintCustomerEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.ReservationPersonalInfoSet;
import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import il.cshaifasweng.OCSFMediatorExample.entities.Customer;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import il.cshaifasweng.OCSFMediatorExample.entities.RestTable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;
import static il.cshaifasweng.OCSFMediatorExample.entities.ReqCategory.BRANCH;
import static il.cshaifasweng.OCSFMediatorExample.entities.RequestType.UPDATE_BRANCH;

public class PersonalDetailsFillingBoundary {

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

    private String type;
    public boolean typeIsSet=false;


    public PersonalDetailsFillingBoundary(String type) {
        this.type = type;
        this.typeIsSet=true;
    }

    public PersonalDetailsFillingBoundary() {}
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
        else if(type =="reservation")
        {
            SimpleClient.getClient().mapReservation.put("name",name);
            SimpleClient.getClient().mapReservation.put("phone",phone);
            SimpleClient.getClient().mapReservation.put("mail",mail);
            Customer customer=new Customer();
            customer.setName(name);
            customer.setEmail(mail);
            customer.setPhone(phone);
            SimpleClient.getClient().resInfo.setCustomer(customer);
            openCreditCardPage();
        }
    }

    @FXML
    void backToReservation(ActionEvent event) {

    }
    @FXML
    void initialize() {
        assert backBtn != null : "fx:id=\"backBtn\" was not injected: check your FXML file 'personalDetailsFilling.fxml'.";
        assert contToCCInfoBtn != null : "fx:id=\"contToCCinfoBtn\" was not injected: check your FXML file 'personalDetailsFilling.fxml'.";
        assert mailTextField != null : "fx:id=\"mailTextField\" was not injected: check your FXML file 'personalDetailsFilling.fxml'.";
        assert nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'personalDetailsFilling.fxml'.";
        assert phoneTextField != null : "fx:id=\"phoneTextField\" was not injected: check your FXML file 'personalDetailsFilling.fxml'.";


    }
    // initialize the map before letting the map page be opened
    public void setType(String type) {
        System.out.println("in set type before sync");
        synchronized (this) {
            if (this.type == null || !this.type.equals(type)) {
                System.out.println("in type map after sync");
                this.type = type;
                this.typeIsSet = true;
                notifyAll();
            }
        }
    }

    public static boolean isValidPhone(String phone) {
        return Pattern.compile("^(\\+\\d{1,3})?\\d{10,15}$").matcher(phone).matches();
    }

    public static boolean isValidEmail(String email) {
        return Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$").matcher(email).matches();
    }
    public void openCreditCardPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("creditCardInfo.fxml"));
            Parent creditCardInfoPageRoot = loader.load();
            // Get the controller and set the type before waiting
            CreditCradInfoBoundary boundary = loader.getController();
            boundary.setType("reservation");  // This should be set before waiting
            synchronized (boundary) {
                while (!boundary.typeIsSet) {
                    System.out.println("Waiting for type to be set...");
                    boundary.wait();  // Waits until notifyAll() is called
                }
            }
            Platform.runLater(() -> {
                try {
                    App.setContent(creditCardInfoPageRoot);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();  // Restore interrupted state
        }
    }

}