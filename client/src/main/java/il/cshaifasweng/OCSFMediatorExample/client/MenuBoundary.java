package il.cshaifasweng.OCSFMediatorExample.client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class MenuBoundary {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BackToHPbtn;

    @FXML
    private Button complaintBtn;

    @FXML
    private Button deliveryBtn;

    @FXML
    private TableColumn<?, ?> imageColum;

    @FXML
    private TableColumn<?, ?> ingredientsColumn;

    @FXML
    private Button menuBtn;

    @FXML
    private Label menuLabel;

    @FXML
    private TableView<?> menuTableView;

    @FXML
    private TableColumn<?, ?> nameColumn;

    @FXML
    private TableColumn<?, ?> preferenceColumn;

    @FXML
    private TableColumn<?, ?> priceColumn;

    @FXML
    private Button reservationBtn;

    @FXML
    private VBox sideBar;

    @FXML
    private Button toggleButtonBranch;

    @FXML
    void BackToHPfunc(ActionEvent event) {switchScreen("Home Page");}

    @FXML
    void displayMenu(ActionEvent event) {switchScreen("Menu");}

    @FXML
    void navToComplaintPage(ActionEvent event) {switchScreen("Complaint");}

    @FXML
    void navToDeliveryPage(ActionEvent event) {switchScreen("Delivery");}

    @FXML
    void navToReservationPage(ActionEvent event) {switchScreen("Reservations");}

    @FXML
    void initialize() {
        assert BackToHPbtn != null : "fx:id=\"BackToHPbtn\" was not injected: check your FXML file 'menu.fxml'.";
        assert complaintBtn != null : "fx:id=\"complaintBtn\" was not injected: check your FXML file 'menu.fxml'.";
        assert deliveryBtn != null : "fx:id=\"deliveryBtn\" was not injected: check your FXML file 'menu.fxml'.";
        assert imageColum != null : "fx:id=\"imageColum\" was not injected: check your FXML file 'menu.fxml'.";
        assert ingredientsColumn != null : "fx:id=\"ingredientsColumn\" was not injected: check your FXML file 'menu.fxml'.";
        assert menuBtn != null : "fx:id=\"menuBtn\" was not injected: check your FXML file 'menu.fxml'.";
        assert menuLabel != null : "fx:id=\"menuLabel\" was not injected: check your FXML file 'menu.fxml'.";
        assert menuTableView != null : "fx:id=\"menuTableView\" was not injected: check your FXML file 'menu.fxml'.";
        assert nameColumn != null : "fx:id=\"nameColumn\" was not injected: check your FXML file 'menu.fxml'.";
        assert preferenceColumn != null : "fx:id=\"preferenceColumn\" was not injected: check your FXML file 'menu.fxml'.";
        assert priceColumn != null : "fx:id=\"priceColumn\" was not injected: check your FXML file 'menu.fxml'.";
        assert reservationBtn != null : "fx:id=\"reservationBtn\" was not injected: check your FXML file 'menu.fxml'.";
        assert sideBar != null : "fx:id=\"sideBar\" was not injected: check your FXML file 'menu.fxml'.";
        assert toggleButtonBranch != null : "fx:id=\"toggleButtonBranch\" was not injected: check your FXML file 'menu.fxml'.";

    }

}
