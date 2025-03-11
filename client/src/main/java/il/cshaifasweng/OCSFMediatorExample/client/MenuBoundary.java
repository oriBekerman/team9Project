package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class MenuBoundary {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BackToHPbtn;

    @FXML
    private TableColumn<?, ?> imageColum;

    @FXML
    private TableColumn<?, ?> ingredientsColumn;

    @FXML
    private Label menuLabel;

    @FXML
    private AnchorPane sideBarPlace;

    @FXML
    private TableView<?> menuTableView;

    @FXML
    private TableColumn<?, ?> nameColumn;

    @FXML
    private TableColumn<?, ?> preferenceColumn;

    @FXML
    private TableColumn<?, ?> priceColumn;


    @FXML
    void BackToHPfunc(ActionEvent event) {switchScreen("Home Page");}


    @FXML
    void initialize() throws IOException {
        assert BackToHPbtn != null : "fx:id=\"BackToHPbtn\" was not injected: check your FXML file 'menu.fxml'.";
        assert imageColum != null : "fx:id=\"imageColum\" was not injected: check your FXML file 'menu.fxml'.";
        assert ingredientsColumn != null : "fx:id=\"ingredientsColumn\" was not injected: check your FXML file 'menu.fxml'.";
        assert menuLabel != null : "fx:id=\"menuLabel\" was not injected: check your FXML file 'menu.fxml'.";
        assert menuTableView != null : "fx:id=\"menuTableView\" was not injected: check your FXML file 'menu.fxml'.";
        assert nameColumn != null : "fx:id=\"nameColumn\" was not injected: check your FXML file 'menu.fxml'.";
        assert preferenceColumn != null : "fx:id=\"preferenceColumn\" was not injected: check your FXML file 'menu.fxml'.";
        assert priceColumn != null : "fx:id=\"priceColumn\" was not injected: check your FXML file 'menu.fxml'.";
        assert sideBarPlace != null : "fx:id=\"sideBarPlace\" was not injected: check your FXML file 'menu.fxml'.";

        Parent sideBarParent = App.loadFXML("sideBarBranch");
        sideBarPlace.getChildren().clear();
        sideBarPlace.getChildren().add(sideBarParent);
    }
}
