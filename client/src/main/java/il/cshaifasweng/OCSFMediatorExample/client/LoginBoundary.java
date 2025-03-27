package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.Events.UserLoginFailedEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.UserLoginSuccessEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.scene.control.Label;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;
import static il.cshaifasweng.OCSFMediatorExample.entities.RequestType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.ReqCategory.*;

public class LoginBoundary {


    public AnchorPane root;
    public Label userLabel;
    public Label passwordLabel;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    @FXML
    private Button backToHPBtn;

    @FXML
    private Button loginBtn;

    @FXML
    private TextField passwordTextF;

    @FXML
    private TextField userNameTextF;

    @FXML
    private Label statusLabel;


    @FXML
    void loginFunc(ActionEvent event) {
        try {
            String username = userNameTextF.getText().trim();
            String password = passwordTextF.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Username or Password cannot be empty.");
                return;
            }

            System.out.println("Attempting to send login request: Username=" + username + ", Password=" + password);

            Request<String> request = new Request<>(LOGIN,CHECK_USER, username + " " + password);
            SimpleClient.getClient().sendToServer(request);
            System.out.println("Login request sent to server.");

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error connecting to server.");
        }
    }

    @Subscribe
    public void handleLoginSuccess(UserLoginSuccessEvent event) {
        SessionManager.getInstance().setUser(event.getUsername(), event.getAuthorization());
        updateUIBasedOnRole();
        switchScreen("Home Page");
    }

    private void updateUIBasedOnRole() {
        String role = SessionManager.getInstance().getAuthorization();
        // Enable or disable UI components based on role, if applicable to this controller
    }


    @Subscribe
    public void handleLoginFailure(UserLoginFailedEvent event) {
        Platform.runLater(() -> {
            System.out.println("Login Failed: " + event.getMessage());
            statusLabel.setText(event.getMessage() != null ? event.getMessage() : "Unknown error");  // Update UI safely
        });
    }


    @FXML
    void passwordFiled(ActionEvent event) {
    }

    @FXML
    void userNameFiled(ActionEvent event) {
    }
    @FXML
    void navToHP(ActionEvent event) {
        switchScreen("Home Page");
    }


    @FXML
    void initialize() {
        System.out.println("LoginController initialized. Registering EventBus...");
        EventBus.getDefault().register(this);
        assert backToHPBtn != null : "fx:id=\"backToHPBtn\" was not injected: check your FXML file 'delivery.fxml'.";
        assert loginBtn != null : "fx:id=\"loginBtn\" was not injected: check your FXML file 'login.fxml'.";
        assert passwordTextF != null : "fx:id=\"passwordTextF\" was not injected: check your FXML file 'login.fxml'.";
        assert statusLabel != null : "fx:id=\"statusLabel\" was not injected: check your FXML file 'login.fxml'.";
        assert userNameTextF != null : "fx:id=\"userNameTextF\" was not injected: check your FXML file 'login.fxml'.";
        setStyle();


    }
    public void setStyle()
    {
        root.setStyle("-fx-background-color: #fbe9d0;");

        // Buttons Styling
        String buttonStyle = "-fx-background-color: #8a6f48;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8px 16px;" +
                "-fx-border-radius: 5px;";

        backToHPBtn.setStyle(buttonStyle);
        loginBtn.setStyle(buttonStyle);

    }

}
