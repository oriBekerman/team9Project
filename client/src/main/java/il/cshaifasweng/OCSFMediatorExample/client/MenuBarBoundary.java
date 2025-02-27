package il.cshaifasweng.OCSFMediatorExample.client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class MenuBarBoundary {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private MenuBar MenuBar;

    @FXML
    void navToDelivery(ActionEvent event) {
        switchScreen("Delivery");
    }

    @FXML
    void navToHP(ActionEvent event) {
        switchScreen("Home Page");
    }

    @FXML
    void navToBranches(ActionEvent event) {
        switchScreen("Branches");
    }

    @FXML
    void navToReservation(ActionEvent event) {
        switchScreen("Reservation");
    }


    @FXML
    void initialize() {
        assert MenuBar != null : "fx:id=\"MenuBar\" was not injected: check your FXML file 'MenuBar.fxml'.";
    }

}

