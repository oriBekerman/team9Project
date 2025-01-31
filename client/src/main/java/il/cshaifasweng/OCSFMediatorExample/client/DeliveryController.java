package il.cshaifasweng.OCSFMediatorExample.client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class DeliveryController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backToHPBtn;

    @FXML
    private TableView<?> deliveryTableView;

    @FXML
    private Button payBtn;

    @FXML
    void navToPay(ActionEvent event) {
        switchScreen("Credit Card Info");
    }

    @FXML
    void navToHP(ActionEvent event) {
        switchScreen("Home Page");
    }

    @FXML
    void initialize() {
        assert backToHPBtn != null : "fx:id=\"backToHPBtn\" was not injected: check your FXML file 'delivery.fxml'.";
        assert deliveryTableView != null : "fx:id=\"deliveryTableView\" was not injected: check your FXML file 'delivery.fxml'.";
        assert payBtn != null : "fx:id=\"payBtn\" was not injected: check your FXML file 'delivery.fxml'.";

    }

}
