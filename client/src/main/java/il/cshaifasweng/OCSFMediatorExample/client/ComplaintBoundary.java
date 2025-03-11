package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class ComplaintBoundary {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backToHPBtn;

    @FXML
    private Button submitBtn;

    @FXML
    private Label branchTitle;

    @FXML
    private TextField contentOfComplaintTextFiled;

    @FXML
    private TextField emailTextFiled;

    @FXML
    private TextField fullNameTextFiled;

    @FXML
    private AnchorPane sideBarPlace;

    @FXML
    void navToHP(ActionEvent event) {switchScreen("Home Page");}

    /////////////////////////////////////////// Of course, this method needs to change when working on the complaint feature.
    @FXML
    void submitToCustomerService(ActionEvent event) {switchScreen("Home Page");}

    @FXML
    void initialize() throws IOException {
        assert backToHPBtn != null : "fx:id=\"backToHPBtn\" was not injected: check your FXML file 'complaint.fxml'.";
        assert branchTitle != null : "fx:id=\"branchTitle\" was not injected: check your FXML file 'complaint.fxml'.";
        assert contentOfComplaintTextFiled != null : "fx:id=\"contentOfComplaintTextFiled\" was not injected: check your FXML file 'complaint.fxml'.";
        assert emailTextFiled != null : "fx:id=\"emailTextFiled\" was not injected: check your FXML file 'complaint.fxml'.";
        assert fullNameTextFiled != null : "fx:id=\"fullNameTextFiled\" was not injected: check your FXML file 'complaint.fxml'.";
        assert sideBarPlace != null : "fx:id=\"sideBarPlace\" was not injected: check your FXML file 'complaint.fxml'.";
        assert submitBtn != null : "fx:id=\"submitBtn\" was not injected: check your FXML file 'complaint.fxml'.";

        Parent sideBarParent = App.loadFXML("sideBarBranch");
        sideBarPlace.getChildren().clear();
        sideBarPlace.getChildren().add(sideBarParent);
    }

}

