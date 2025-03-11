package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchListSentEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchSelectedEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import il.cshaifasweng.OCSFMediatorExample.entities.EmployeeType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class SideBarPrimaryBoundary {

    public Branch branch;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private VBox sideBar;

    @FXML
    private ImageView MOMSImage;

    @FXML
    private Button loginBtn;

    @FXML
    private Button menuBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    private Button updateMenuBtn;

    @FXML
    private Button toggleButtonBranch;
    private Popup popup = new Popup();

    public List<Branch> branchList = null;
    public boolean branchListInit = false;
    private final Object lock = new Object();

    @FXML
    private Button toggleButtonReports;

    @FXML
    void navToMenu(ActionEvent event) {switchScreen("Menu");}

    @FXML
    void navToLogin(ActionEvent event) {switchScreen("Login");}

//    @FXML
//    void navToLogOut(ActionEvent event) {switchScreen("LogOut");}
    @FXML
   void navToUpdateMenu(ActionEvent event) {switchScreen("Update Menu");}


//    @FXML
//    public void navToMenu(ActionEvent actionEvent) {
//        switchScreen("menu");
//        try {
//            App.setRoot("menu");
//            SimpleClient.getClient().displayBranchMenu(branch);
////            Menu menu = new Menu(branch.getBranchMenuItems());
////            menu.printMenu();
////            SimpleClient.getClient().showMenu(menu);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @FXML
    void navToLogOut(ActionEvent event) {
        // Call the logout method in SimpleClient to clear active user
        SimpleClient.getClient().logout();

        // Hide the logout button and show the login button again
        logoutBtn.setVisible(false);
        loginBtn.setVisible(true);
        // Hide the Update button after logging out
        updateMenuBtn.setVisible(false);

        // Hide the Reports button after logging out
        toggleButtonReports.setVisible(false);
    }

    @FXML
    void initialize() {
        assert MOMSImage != null : "fx:id=\"MOMSImage\" was not injected: check your FXML file 'sideBarPrimary.fxml'.";
        assert loginBtn != null : "fx:id=\"loginBtn\" was not injected: check your FXML file 'sideBarPrimary.fxml'.";
        assert logoutBtn != null : "fx:id=\"logoutBtn\" was not injected: check your FXML file 'sideBarPrimary.fxml'.";
        assert menuBtn != null : "fx:id=\"menuBtn\" was not injected: check your FXML file 'sideBarPrimary.fxml'.";
        assert sideBar != null : "fx:id=\"sideBar\" was not injected: check your FXML file 'sideBarPrimary.fxml'.";
        assert toggleButtonBranch != null : "fx:id=\"toggleButtonBranch\" was not injected: check your FXML file 'sideBarPrimary.fxml'.";
        assert toggleButtonReports != null : "fx:id=\"toggleButtonReports\" was not injected: check your FXML file 'sideBarPrimary.fxml'.";
        assert updateMenuBtn != null : "fx:id=\"updateMenuBtn\" was not injected: check your FXML file 'sideBarPrimary.fxml'.";

        // Set the button action here
        EventBus.getDefault().register(this);
        SimpleClient.getClient().getBranchList(); // Request branch list from server

        toggleButtonBranch.setOnAction(e -> {
            System.out.println("Button clicked - showing popup");
            GetBranchListPopup();
        });


        // This section display the image of mamasKitchen
        String imagePath = "il/cshaifasweng/OCSFMediatorExample/client/mamasKitchen.jpg";
        Image image = new Image(imagePath);
        MOMSImage.setImage(image);


        // Check if the user is logged in (activeUser is not null)
        if (SimpleClient.getClient().getActiveUser() != null) {
            // If logged in, show logout button and hide login button
            logoutBtn.setVisible(true);
            loginBtn.setVisible(false);
            toggleButtonReports.setVisible(false);
            // Check if the user is a "DIETITIAN" and display the Update button if true
            if (SimpleClient.getClient().getActiveUser().getEmployeeType() == EmployeeType.DIETITIAN) {
                System.out.println("Active User: " + SimpleClient.getClient().getActiveUser().getUsername());
                updateMenuBtn.setVisible(true);  // Show Update button if user is a DIETITIAN
            } else {
                updateMenuBtn.setVisible(false);  // Hide Update button if user is not a DIETITIAN
            }
        } else {
            // If not logged in, show login button and hide logout button
            logoutBtn.setVisible(false);
            loginBtn.setVisible(true);
            updateMenuBtn.setVisible(false); // Hide Update button if not logged in
            toggleButtonReports.setVisible(false); // Hide Reports button if not logged in
        }

    }

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
