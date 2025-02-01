package il.cshaifasweng.OCSFMediatorExample.client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class LoginController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button loginBtn;

    @FXML
    private TextField passwordTextF;

    @FXML
    private TextField userNameTextF;



    @FXML
    void loginFunc(ActionEvent event) {
        switchScreen("Home Page");
    }

    @FXML
    void passwordFiled(ActionEvent event) {

    }

    @FXML
    void userNameFiled(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert loginBtn != null : "fx:id=\"loginBtn\" was not injected: check your FXML file 'login.fxml'.";
        assert passwordTextF != null : "fx:id=\"passwordTextF\" was not injected: check your FXML file 'login.fxml'.";
        assert userNameTextF != null : "fx:id=\"userNameTextF\" was not injected: check your FXML file 'login.fxml'.";


    }

}
