package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Delivery;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class PersonalDetailsFillingDeliveryBoundary {

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
    void contToCCinfoFill(ActionEvent event) {
        switchScreen("Credit Card Info");
    }

    @FXML
    void backToDelivery(ActionEvent event) {
        switchScreen("Reservation");
    }

    private Delivery delivery = null;

    @FXML
    void initialize() {
        assert backBtn != null : "fx:id=\"backBtn\" was not injected: check your FXML file 'personalDetailsFilling.fxml'.";
        assert contToCCInfoBtn != null : "fx:id=\"contToCCinfoBtn\" was not injected: check your FXML file 'personalDetailsFilling.fxml'.";
        assert mailTextField != null : "fx:id=\"mailTextField\" was not injected: check your FXML file 'personalDetailsFilling.fxml'.";
        assert nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'personalDetailsFilling.fxml'.";
        assert phoneTextField != null : "fx:id=\"phoneTextField\" was not injected: check your FXML file 'personalDetailsFilling.fxml'.";

    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        if (delivery != null) {
            System.out.println("Setting delivery details: " + delivery.toString());
        }
    }



}
