package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.Events.MenuEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static il.cshaifasweng.OCSFMediatorExample.client.App.*;

public class BranchPageBoundary
{
    public boolean branchIsSet = false;
    private SideBarBranchBoundary sidebarController;
    public Branch branch;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backToHPBtn;

    @FXML
    private Label branchTitle;

    @FXML
    private Label closeHour;

    @FXML
    private Label openHour;

    @FXML
    private Label openingHoursLabel;

    @FXML
    private AnchorPane sideBarPlace;


    private TableView<MenuItem> menuTableView;



//    public BranchPageBoundary()
//    {
//        if (!EventBus.getDefault().isRegistered(this))
//        {
//            EventBus.getDefault().register(this);
//        }
//    }



    @FXML
    void navToHP(ActionEvent event)
    {
//        onExit();
        sidebarController.onExit();  // ← ADD THIS LINE
        switchScreen("Home Page");
    }


    @FXML
    void initialize()
    {
        assert backToHPBtn != null : "fx:id=\"backToHPBtn\" was not injected: check your FXML file 'Branch.fxml'.";
        assert branchTitle != null : "fx:id=\"branchTitle\" was not injected: check your FXML file 'Branch.fxml'.";
        assert closeHour != null : "fx:id=\"closeHour\" was not injected: check your FXML file 'Branch.fxml'.";
        assert openHour != null : "fx:id=\"openHour\" was not injected: check your FXML file 'Branch.fxml'.";
        assert openingHoursLabel != null : "fx:id=\"openingHoursLabel\" was not injected: check your FXML file 'Branch.fxml'.";
        assert sideBarPlace != null : "fx:id=\"sideBarPlace\" was not injected: check your FXML file 'Branch.fxml'.";

//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("sideBarBranch.fxml"));
            Parent sideBarBranchRoot = loader.load();
//            sidebarController = loader.getController();
            sidebarController = SideBarBranchBoundary.getInstance();


            sideBarPlace.getChildren().clear();
            sideBarPlace.getChildren().add(sideBarBranchRoot);

            Platform.runLater(() -> {
                if (!EventBus.getDefault().isRegistered(sidebarController)) {
                    EventBus.getDefault().register(sidebarController);
                    System.out.println("✅ [BranchPageBoundary] Sidebar registered to EventBus");
                }

                SimpleClient.getClient().getBranchList();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onMenuEvent(MenuEvent event) {
        System.out.println("[BranchPageBoundary] Menu received for branch: " + branch.getName());
    }


    // Method to set the branch data
    public void setBranch(Branch branch) {
        if (branch == null) {
            System.out.println("[BranchPageBoundary] ERROR: Received null branch!");
            return;
        }

        this.branch = branch;
        System.out.println("[BranchPageBoundary] Branch explicitly set: " + branch.getName());

        branchTitle.setText("Branch: " + branch.getName());
        openHour.setText(branch.getOpeningTime());
        closeHour.setText(branch.getClosingTime());
        System.out.println("[BranchPageBoundary] Branch explicitly set: " + branch.getName());

        updateUI();

//        try {
//            SimpleClient.getClient().displayBranchMenu(branch);
//            System.out.println("Menu requested for branch: " + branch.getName());
//        } catch (IOException e) {
//            System.err.println("Error displaying branch menu: " + e.getMessage());
//        }
        branchIsSet = true;

        if (sidebarController != null) {
            sidebarController.setBranch(branch);
            sidebarController.getUserAuthorizedTools();
            System.out.println("[BranchPageBoundary] Sidebar set explicitly with branch: " + branch.getName());
        } else {
            System.out.println("[BranchPageBoundary] ERROR: sidebarController is null!");
        }
    }

    // Method to update UI based on the branch data
    private void updateUI() {
        if (branch != null && branchTitle != null)
        {
            branchTitle.setText("Branch: " + branch.getName());
            openingHoursLabel.setText("opening hours: " + branch.getOpeningTime() + " - " + branch.getClosingTime());
        }
    }


    public void onExit()
    {
        if (sidebarController != null) {
            sidebarController.onExit(); // Add this!
        }

        if (EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().unregister(this);
            System.out.println("Unregistered from EventBus");
        }

    }
}
