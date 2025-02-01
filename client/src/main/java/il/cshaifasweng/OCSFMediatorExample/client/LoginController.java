package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.Subscribe;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;
import static il.cshaifasweng.OCSFMediatorExample.entities.Request.RequestType.CHECK_USER;
import static il.cshaifasweng.OCSFMediatorExample.entities.Request.RequestType.DISPLAY_MENU;


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


    // Event handler for MenuEvent
//    @Subscribe
//    public void (updateDishEvent event) {
//        try {
//            Request request=new Request<>(CHECK_USER);
//            request.
//            SimpleClient.getClient().sendToServer(request);
//            menuTableView.refresh();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }



    @FXML
    void initialize() {
        assert loginBtn != null : "fx:id=\"loginBtn\" was not injected: check your FXML file 'login.fxml'.";
        assert passwordTextF != null : "fx:id=\"passwordTextF\" was not injected: check your FXML file 'login.fxml'.";
        assert userNameTextF != null : "fx:id=\"userNameTextF\" was not injected: check your FXML file 'login.fxml'.";


    }

}
