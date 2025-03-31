package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.greenrobot.eventbus.EventBus;

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

    private SideBarBranchBoundary sidebarController;

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
        System.out.println("[BranchPageBoundary] Back to Home Page clicked");
        cleanup();  // explicitly unsubscribe from EventBus
        switchScreen("Home Page");
    }


    @FXML
    void initialize() throws IOException {
        updateUI();
        assert backToHPBtn != null : "fx:id=\"backToHPBtn\" was not injected: check your FXML file 'BranchPage.fxml'.";
        assert sideBarPlace != null : "fx:id=\"sideBarPlace\" was not injected: check your FXML file 'new.fxml'.";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("sideBarBranch.fxml"));
            Parent sideBarBranchRoot = loader.load();
            sidebarController = loader.getController();

            sideBarPlace.getChildren().clear();
            sideBarPlace.getChildren().add(sideBarBranchRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to set the branch data explicitly after initialization
    public void setBranch(Branch branch) {
        if (branch == null) {
            System.out.println("[BranchPageBoundary] ERROR: Received null branch!");
            return;
        }
        this.branch = branch;
        branchTitle.setText("Branch: " + branch.getName());
        openHour.setText(branch.getOpeningTime());
        closeHour.setText(branch.getClosingTime());
        System.out.println("[BranchPageBoundary] Branch explicitly set: " + branch.getName());

        if (sidebarController != null) {
            sidebarController.setBranch(branch);
            System.out.println("[BranchPageBoundary] Sidebar set explicitly with branch: " + branch.getName());
        } else {
            System.out.println("[BranchPageBoundary] ERROR: sidebarController is null!");
        }
    }


    // Method to update UI based on the branch data
    private void updateUI() {
        if (branch != null && branchTitle != null) {
            branchTitle.setText("Branch: " + branch.getName());
            openingHoursLabel.setText("opening hours: " + branch.getOpeningTime() + " - " + branch.getClosingTime());
        }
    }

    public void cleanup() {
        EventBus.getDefault().unregister(this);
    }
}