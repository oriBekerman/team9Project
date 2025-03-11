package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class BranchPageBoundary {

    public boolean branchIsSet = false;
    public Button deliveryBtn;
    public Label openingHoursLabel;
    public Label branchTitle;
    public Label openHour;
    public Label closeHour;

    public BranchPageBoundary() {};
    public Branch branch;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backToHPBtn;

    @FXML
    private AnchorPane sideBarPlace;

    @FXML
    void navToHP(ActionEvent event) {
        switchScreen("Home Page");
    }


    @FXML
    void initialize() throws IOException {
        updateUI();
        assert backToHPBtn != null : "fx:id=\"backToHPBtn\" was not injected: check your FXML file 'BranchPage.fxml'.";
        assert sideBarPlace != null : "fx:id=\"sideBarPlace\" was not injected: check your FXML file 'new.fxml'.";

//        EventBus.getDefault().register(this);

        Parent sideBarParent = App.loadFXML("sideBarBranch");
        sideBarPlace.getChildren().clear();
        sideBarPlace.getChildren().add(sideBarParent);
    }

    // Method to set the branch data
    public void setBranch(Branch branch) {
        this.branch = branch;
        branchTitle.setText("Branch: " + branch.getName());
        openHour.setText(branch.getOpeningTime());
        closeHour.setText(branch.getClosingTime());
        branchIsSet = true;
        System.out.println("in branch page controller");
        System.out.println("opening: " + branch.getOpeningTime());
    }

    // Method to update UI based on the branch data
    private void updateUI() {
        if (branch != null && branchTitle != null) {
            branchTitle.setText("Branch: " + branch.getName());
            openingHoursLabel.setText("opening hours: " + branch.getOpeningTime() + " - " + branch.getClosingTime());
        }
    }
}
