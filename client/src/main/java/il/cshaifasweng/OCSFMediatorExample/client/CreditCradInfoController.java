package il.cshaifasweng.OCSFMediatorExample.client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class CreditCradInfoController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backBtn;

    @FXML
    private Button paymentBtn;

    @FXML
    void backToPersonalD(ActionEvent event) {
        switchScreen("Personal Details Filling");
    }

    @FXML
    void checkPayment(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert backBtn != null : "fx:id=\"backBtn\" was not injected: check your FXML file 'creditCardInfo.fxml'.";
        assert paymentBtn != null : "fx:id=\"paymentBtn\" was not injected: check your FXML file 'creditCardInfo.fxml'.";


    }

}
