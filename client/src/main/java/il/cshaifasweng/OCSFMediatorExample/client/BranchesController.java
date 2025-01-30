package il.cshaifasweng.OCSFMediatorExample.client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class BranchesController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backToHPBtn;

    @FXML
    private Button haifaBBtn;

    @FXML
    private Button jersualemBtn;

    @FXML
    private Button telAvivBtn;

    @FXML
    private Button zikhronBtn;

    @FXML
    void navToHP(ActionEvent event) {
        switchScreen("Home Page");
    }

    @FXML
    void navToHaifaBranch(ActionEvent event) {

    }

    @FXML
    void navToJersualemBranch(ActionEvent event) {

    }

    @FXML
    void navToTelAvivBranch(ActionEvent event) {

    }

    @FXML
    void navToZikhronBranch(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert backToHPBtn != null : "fx:id=\"backToHPBtn\" was not injected: check your FXML file 'branches.fxml'.";
        assert haifaBBtn != null : "fx:id=\"haifaBBtn\" was not injected: check your FXML file 'branches.fxml'.";
        assert jersualemBtn != null : "fx:id=\"jersualemBtn\" was not injected: check your FXML file 'branches.fxml'.";
        assert telAvivBtn != null : "fx:id=\"telAvivBtn\" was not injected: check your FXML file 'branches.fxml'.";
        assert zikhronBtn != null : "fx:id=\"zikhronBtn\" was not injected: check your FXML file 'branches.fxml'.";

    }

}
