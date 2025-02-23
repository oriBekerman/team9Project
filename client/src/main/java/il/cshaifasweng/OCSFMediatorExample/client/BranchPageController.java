package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.Events.MenuEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import il.cshaifasweng.OCSFMediatorExample.entities.Menu;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class BranchPageController {

    public boolean branchIsSet = false;
    public Button reservationBtn;
    public Button deliveryBtn;
    public Button complaintBtn;
    public Label openingHoursLabel;
    public Label branchTitle;
    public VBox sideBar;
    public Label openHour;
    public Label closeHour;
    public Button menuBtn;

    public BranchPageController() {};
    public Branch branch;
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
        updateUI();
        assert backToHPBtn != null : "fx:id=\"backToHPBtn\" was not injected: check your FXML file 'BranchPage.fxml'.";
        assert haifaBBtn != null : "fx:id=\"haifaBBtn\" was not injected: check your FXML file 'BranchPage.fxml'.";
        assert jersualemBtn != null : "fx:id=\"jersualemBtn\" was not injected: check your FXML file 'BranchPage.fxml'.";
        assert telAvivBtn != null : "fx:id=\"telAvivBtn\" was not injected: check your FXML file 'BranchPage.fxml'.";
        assert zikhronBtn != null : "fx:id=\"zikhronBtn\" was not injected: check your FXML file 'BranchPage.fxml'.";

    }

    public void navToReservationPage(ActionEvent actionEvent) {
    }

    public void navTodeliveryPage(ActionEvent actionEvent) {
    }

    public void navToComplaintPage(ActionEvent actionEvent) {
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

    public void displayMenu(ActionEvent actionEvent) {
        switchScreen("secondary");
        try {
            App.setRoot("secondary");
            SimpleClient.getClient().displayBranchMenu(branch);
//            Menu menu = new Menu(branch.getBranchMenuItems());
//            menu.printMenu();
//            SimpleClient.getClient().showMenu(menu);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//change