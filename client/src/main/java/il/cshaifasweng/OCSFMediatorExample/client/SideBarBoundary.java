package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchListSentEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchSelectedEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class SideBarBoundary {

    public boolean branchIsSet = false;
    public Button reservationBtn;
    public Button deliveryBtn;
    public Button complaintBtn;
    public Label branchTitle;
    public VBox sideBar;
    public Button menuBtn;

    public SideBarBoundary() {};
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
    private Button jerusalemBtn;

    @FXML
    private Button telAvivBtn;

    @FXML
    private Button zikhronBtn;


    @FXML
    private Button toggleButtonBranch;
    private Popup popup = new Popup();

    public List<Branch> branchList = null;
    public boolean branchListInit = false;
    private final Object lock = new Object();

    @FXML
    void navToHP(ActionEvent event) {
        switchScreen("Home Page");
    }

    @FXML
    void navToComplaintPage(ActionEvent event) {switchScreen("Complaint");}

    @FXML
    void navToDeliveryPage(ActionEvent event) {switchScreen("Delivery");}

    @FXML
    void navToReservationPage(ActionEvent event) {switchScreen("Reservation");}

    @FXML
    void initialize() {
        updateUI();
        assert backToHPBtn != null : "fx:id=\"backToHPBtn\" was not injected: check your FXML file 'BranchPage.fxml'.";
        assert haifaBBtn != null : "fx:id=\"haifaBBtn\" was not injected: check your FXML file 'BranchPage.fxml'.";
        assert jerusalemBtn != null : "fx:id=\"jerusalemBtn\" was not injected: check your FXML file 'BranchPage.fxml'.";
        assert telAvivBtn != null : "fx:id=\"telAvivBtn\" was not injected: check your FXML file 'BranchPage.fxml'.";
        assert zikhronBtn != null : "fx:id=\"zikhronBtn\" was not injected: check your FXML file 'BranchPage.fxml'.";
        assert toggleButtonBranch != null : "fx:id=\"toggleButtonBranch\" was not injected: check your FXML file 'primary.fxml'.";

        /// ///////////////////////////////////////////////////////////////
        // I think this block of code is regarding the popup of the branchesList
        // Set the button action here
        EventBus.getDefault().register(this);
        SimpleClient.getClient().getBranchList(); // Request branch list from server
        toggleButtonBranch.setOnAction(e -> {
            System.out.println("Button clicked - showing popup");
            GetBranchListPopup();
        });
        /// ///////////////////////////////////////////////////////////////



    }

    // Method to set the branch data
    public void setBranch(Branch branch) {
        this.branch = branch;
        branchTitle.setText("Branch: " + branch.getName());
        branchIsSet = true;
        System.out.println("in branch page controller");
        System.out.println("opening: " + branch.getOpeningTime());
    }

    // Method to update UI based on the branch data
    private void updateUI() {
        if (branch != null && branchTitle != null) {
            branchTitle.setText("Branch: " + branch.getName());
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

    // shir may - I duplicated those functions from the PrimaryBoundary
    // since I want the branch list to be also in the branch(es) page ( when clicking on the btn of " Our Branches")
    /// /////////////////////////////////////////////////////////////////////////////////////////////

    //get list of brunches pop up
    private void GetBranchListPopup() {
        synchronized (lock) {
            if (!branchListInit) {
                try {
                    SimpleClient.getClient().getBranchList();
                    while (!branchListInit) {
                        lock.wait();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Thread interrupted while waiting for branch list.");
                    return;
                }
            }
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BranchList.fxml"));
            Parent popupContent = loader.load();

            // Get BranchListController instance and set branches
            BranchListBoundary controller = loader.getController();
            controller.setBranches(branchList);

            popup.getContent().clear();
            popup.getContent().add(popupContent);
            popup.setAutoHide(true);
            // Ensure popup shows correctly
            if (toggleButtonBranch.getScene() != null) {
                popup.show(toggleButtonBranch.getScene().getWindow(),
                        toggleButtonBranch.localToScreen(0, 0).getX(),
                        toggleButtonBranch.localToScreen(0, 0).getY() + toggleButtonBranch.getHeight());
            } else {
                System.out.println("toggleButtonBranch scene is NULL - cannot display popup");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void getPopup(ActionEvent actionEvent) {
        System.out.println("getPopup");
        GetBranchListPopup();
    }

    // Handle the branch selected from the list
    @Subscribe
    public void onBranchSelectedEvent(BranchSelectedEvent event) {
        System.out.println("Branch selected: " + event.getBranch().getName());
        Branch branch = event.getBranch();
        if (branch == null) {
            System.out.println("branch is null");
        }
        openBranchPage(branch);
    }

    //open selected branch page
    private void openBranchPage(Branch branch) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Branch.fxml"));
            Parent branchPageRoot = loader.load();
            // Get the controller and pass the branch
            BranchPageBoundary controller = loader.getController();
            controller.setBranch(branch);
            if (controller.branchIsSet) {
                System.out.println("branch is already set");
            }
            while (!controller.branchIsSet) {
                System.out.println("Waiting for branch to be set");
            }
            App.setContent(branchPageRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //handle branch list sent
    @Subscribe
    public void onBranchListSentEvent(BranchListSentEvent event) {
        synchronized (lock) {
            this.branchList = event.branches;
            this.branchListInit = true;
            System.out.println("onBranchesSentEvent");
            lock.notifyAll(); // Notify waiting threads that branches are initialized
        }
    }
/// /////////////////////////////////////////////////////////////////////////////////////////////
}
