package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class ReportBoundary {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BackToHPbtn;

    @FXML
    private Label HomePageLabel;

    @FXML
    private Label WelcomeLabel;

    @FXML
    private BarChart<?, ?> complaintsChart;

    @FXML
    private TableView<?> reportTableView;

    @FXML
    private AnchorPane sideBarPrimaryPlace;


    @FXML
    void BackToHPfunc(ActionEvent event) {
        switchScreen("Home Page");
    }


    @FXML
    void initialize() throws IOException {
        assert BackToHPbtn != null : "fx:id=\"BackToHPbtn\" was not injected: check your FXML file 'report.fxml'.";
        assert HomePageLabel != null : "fx:id=\"HomePageLabel\" was not injected: check your FXML file 'report.fxml'.";
        assert WelcomeLabel != null : "fx:id=\"WelcomeLabel\" was not injected: check your FXML file 'report.fxml'.";
        assert complaintsChart != null : "fx:id=\"complaintsChart\" was not injected: check your FXML file 'report.fxml'.";
        assert reportTableView != null : "fx:id=\"reportTableView\" was not injected: check your FXML file 'report.fxml'.";
        assert sideBarPrimaryPlace != null : "fx:id=\"sideBarPrimaryPlace\" was not injected: check your FXML file 'report.fxml'.";

        Parent sideBarParent = App.loadFXML("sideBarBranch");
        sideBarPrimaryPlace.getChildren().clear();
        sideBarPrimaryPlace.getChildren().add(sideBarParent);

    }
}
