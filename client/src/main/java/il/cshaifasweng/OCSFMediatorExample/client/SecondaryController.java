package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SecondaryController {
    //
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BackToHPbtn;

    @FXML
    private Button SaveBtn;

    @FXML
    private Button UpdatePriceBtn;

    @FXML
    void BackToHPfunc(ActionEvent event) throws IOException {
        App.setRoot("primary");
    }

    @FXML
    void SaveTheUpdateMenu(ActionEvent event) {

    }

    @FXML
    void UpdateTheMenu(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert BackToHPbtn != null : "fx:id=\"BackToHPbtn\" was not injected: check your FXML file 'secondary.fxml'.";
        assert SaveBtn != null : "fx:id=\"SaveBtn\" was not injected: check your FXML file 'secondary.fxml'.";
        assert UpdatePriceBtn != null : "fx:id=\"UpdatePriceBtn\" was not injected: check your FXML file 'secondary.fxml'.";

    }

}
