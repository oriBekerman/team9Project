package il.cshaifasweng.OCSFMediatorExample.client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class New {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backToHPBtn;

    @FXML
    private Label branchTitle;

    @FXML
    void navToHP(ActionEvent event) { switchScreen("Home Page");}

    @FXML
    void initialize() {
        assert backToHPBtn != null : "fx:id=\"backToHPBtn\" was not injected: check your FXML file 'new.fxml'.";
        assert branchTitle != null : "fx:id=\"branchTitle\" was not injected: check your FXML file 'new.fxml'.";
    }

}
