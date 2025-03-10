package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.entities.ReqCategory;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import il.cshaifasweng.OCSFMediatorExample.entities.RequestType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

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
    private Button getDeliveryBtn;

    @FXML
    void getDeliveryRequest(ActionEvent event) throws IOException {
        // Create a request to get the delivery with order number 1
        ReqCategory category = ReqCategory.DELIVERY;  // Delivery category
        RequestType requestType = RequestType.GET_DELIVERY; // Enum constant for delivery request
        Integer orderNumber = 1; // Example order number

        // Create the request object
        Request<Integer> request = new Request<>(category, requestType, orderNumber);

        // Send the request to the server
        SimpleClient.getClient().sendToServer(request);

        // You can optionally add a log message to confirm the request was sent
        System.out.println("Delivery request sent with order number: " + orderNumber);
    }

    @FXML
    void initialize() throws IOException {
        assert backToHPBtn != null : "fx:id=\"backToHPBtn\" was not injected: check your FXML file 'delivery.fxml'.";
        assert deliveryTableView != null : "fx:id=\"deliveryTableView\" was not injected: check your FXML file 'delivery.fxml'.";
        assert payBtn != null : "fx:id=\"payBtn\" was not injected: check your FXML file 'delivery.fxml'.";


    }

}
