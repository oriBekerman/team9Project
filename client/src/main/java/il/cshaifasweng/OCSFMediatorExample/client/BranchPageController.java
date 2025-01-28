
package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class BranchPageController {

    public TextArea sidebarMenu;
    public Button menuBtn;
    @FXML // fx:id="BranchMenuButton"
    private MenuItem BranchMenuButton; // Value injected by FXMLLoader

    @FXML // fx:id="branchNameLabel"
    private Label branchNameLabel; // Value injected by FXMLLoader

    @FXML // fx:id="closingHour"
    private Label closingHour; // Value injected by FXMLLoader

    @FXML // fx:id="menuBarButton"
    private Button menuBarButton; // Value injected by FXMLLoader

    @FXML // fx:id="openingHour"
    private Label openingHour; // Value injected by FXMLLoader

    @FXML // fx:id="openingHoursLabel"
    private Label openingHoursLabel; // Value injected by FXMLLoader

    @FXML // fx:id="sidePopBar"
    private ContextMenu sidePopBar; // Value injected by FXMLLoader

   Branch branch;
   String branchName;

    public  void setBranch(Branch branch) {
        this.branch=branch;
    }

    @FXML
    void setHours(InputMethodEvent event) {
        openingHour.setText(branch.getOpeningTime());
        closingHour.setText(branch.getClosingTime());
    }

//need to change to actual branch
    @FXML
    void openBranchMenu(ActionEvent event) {
        try {
            App.setRoot("secondary");
            SimpleClient.getClient().displayNetworkMenu();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

