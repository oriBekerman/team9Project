package il.cshaifasweng.OCSFMediatorExample.client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;


import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;


public class DeliveryBoundary {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backToHPBtn;

    @FXML
    private TableView<?> deliveryTableView;

    @FXML
    private Label deliveryTitle;

    @FXML
    private TableColumn<?, ?> ingredientsColumn;

    @FXML
    private TableColumn<?, ?> nameColumn;

    @FXML
    private Button personalDITSBtn;

    @FXML
    private TableColumn<?, ?> preferenceColumn;

    @FXML
    private AnchorPane priceColumn;

    @FXML
    private TableColumn<?, ?> quantityColumn;
    @FXML
    void navToPersonalDFill(ActionEvent event) {
        switchScreen("Personal Details Filling");
    }

    @FXML
    void navToHP(ActionEvent event) {
        switchScreen("Home Page");
    }


    @FXML
    void initialize() {
        assert backToHPBtn != null : "fx:id=\"backToHPBtn\" was not injected: check your FXML file 'delivery.fxml'.";
        assert deliveryTableView != null : "fx:id=\"deliveryTableView\" was not injected: check your FXML file 'delivery.fxml'.";
        assert deliveryTitle != null : "fx:id=\"deliveryTitle\" was not injected: check your FXML file 'delivery.fxml'.";
        assert personalDITSBtn != null : "fx:id=\"personalDITSBtn\" was not injected: check your FXML file 'delivery.fxml'.";
    }
}
