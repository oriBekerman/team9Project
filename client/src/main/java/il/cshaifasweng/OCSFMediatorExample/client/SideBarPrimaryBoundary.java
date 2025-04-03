package il.cshaifasweng.OCSFMediatorExample.client;
import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchListSentEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchSelectedEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.EmployeeType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class SideBarPrimaryBoundary {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ImageView MOMSImage;

    @FXML
    private Button cancelDelivery;

    @FXML
    private Button complaintsTableBtn;

    @FXML
    private Button givePermitBtn;

    @FXML
    private Button loginBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    private Button menuBtn;

    @FXML
    private Button reservationBtn;

    @FXML
    private VBox sideBar;

    @FXML
    private Button subCompBtn;

    @FXML
    private Button updateMenuBtn;

    @FXML
    private Button toggleButtonBranch;
    private Popup popup = new Popup();

    public List<Branch> branchList = null;
    public boolean branchListInit = false;
    private final Object lock = new Object();

    public Branch branch;
    private boolean registered = false;

    @FXML
    void givePermit(ActionEvent event)
    {
        try
        {
            Request<Void> request = new Request<>(ReqCategory.PERMIT_GRANTED, RequestType.PERMISSION_REQUEST, null);
            SimpleClient.getClient().sendToServer(request);
        } catch (IOException e)
        {
            e.printStackTrace();
            showErrorMessage("Network error occurred while granting the permit.");
        }
    }

    private void showErrorMessage(String message) {
        System.out.println("Error: " + message);
    }

    @FXML
    void navToLogin(ActionEvent event) {
        onExit();
        switchScreen("Login");
    }

    @FXML
    void navToDeliv(ActionEvent event)
    {
        onExit();
        switchScreen("Delivery");
        try
        {
            SimpleClient.getClient().displayNetworkMenu();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void cancelDel(ActionEvent event) {
        switchScreen("CancelDelivery");
    }

    @FXML
    void navToReservation(ActionEvent event)
    {
        switchScreen("Reservation");
    }

    @FXML
    public void navToMenu(ActionEvent actionEvent) {
        onExit();
        switchScreen("secondary");
        try
        {
            App.setRoot("secondary");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void navToLogOut(ActionEvent event) {
        // Call the logout method in SimpleClient to clear active user
        SimpleClient.getClient().logout();

        // Hide the logout button and show the login button again
        logoutBtn.setVisible(false);
        loginBtn.setVisible(true);
        // Hide the Update button after logging out
        updateMenuBtn.setVisible(false);

    }


    @FXML
    void initialize() {
        assert MOMSImage != null : "fx:id=\"MOMSImage\" was not injected: check your FXML file 'sideBarPrimary.fxml'.";
        assert cancelDelivery != null : "fx:id=\"cancelDelivery\" was not injected: check your FXML file 'sideBarPrimary.fxml'.";
        assert complaintsTableBtn != null : "fx:id=\"complaintsTableBtn\" was not injected: check your FXML file 'sideBarPrimary.fxml'.";
        assert givePermitBtn != null : "fx:id=\"givePermitBtn\" was not injected: check your FXML file 'sideBarPrimary.fxml'.";
        assert loginBtn != null : "fx:id=\"loginBtn\" was not injected: check your FXML file 'sideBarPrimary.fxml'.";
        assert logoutBtn != null : "fx:id=\"logoutBtn\" was not injected: check your FXML file 'sideBarPrimary.fxml'.";
        assert menuBtn != null : "fx:id=\"menuBtn\" was not injected: check your FXML file 'sideBarPrimary.fxml'.";
        assert reservationBtn != null : "fx:id=\"reservationBtn\" was not injected: check your FXML file 'sideBarPrimary.fxml'.";
        assert sideBar != null : "fx:id=\"sideBar\" was not injected: check your FXML file 'sideBarPrimary.fxml'.";
        assert subCompBtn != null : "fx:id=\"subCompBtn\" was not injected: check your FXML file 'sideBarPrimary.fxml'.";
        assert toggleButtonBranch != null : "fx:id=\"toggleButtonBranch\" was not injected: check your FXML file 'sideBarPrimary.fxml'.";
        assert updateMenuBtn != null : "fx:id=\"updateMenuBtn\" was not injected: check your FXML file 'sideBarPrimary.fxml'.";


        if (!registered) {
            EventBus.getDefault().register(this);
            registered = true;
            SimpleClient.getClient().getBranchList();
        }

        toggleButtonBranch.setOnAction(e -> {
            System.out.println("[SideBarPrimaryBoundary- initialize]Button clicked - showing branch list popup");
            GetBranchListPopup();
        });


        // This section display the image of mamasKitchen
        String imagePath = "il/cshaifasweng/OCSFMediatorExample/client/mamasKitchen.jpg";
        Image image = new Image(imagePath);
        MOMSImage.setImage(image);

        if (SimpleClient.getClient().getActiveUser() != null) {
            logoutBtn.setVisible(true);
            loginBtn.setVisible(false);
            updateMenuBtn.setVisible(false);
            givePermitBtn.setVisible(false);
            complaintsTableBtn.setVisible(false);
            getUserAuthorizedTools();
        } else {
            logoutBtn.setVisible(false);
            loginBtn.setVisible(true);
            updateMenuBtn.setVisible(false);
            givePermitBtn.setVisible(false);
            complaintsTableBtn.setVisible(false);
        }
        toggleButtonBranch.setOnAction(e ->
        {
            GetBranchListPopup();
        });
//        // Check if the user is logged in (activeUser is not null)
//        if (SimpleClient.getClient().getActiveUser() != null) {
//            // If logged in, show logout button and hide login button
//            logoutBtn.setVisible(true);
//            loginBtn.setVisible(false);
//            // Check if the user is a "DIETITIAN" and display the Update button if true
//            if (SimpleClient.getClient().getActiveUser().getEmployeeType() == EmployeeType.DIETITIAN) {
//                System.out.println("Active User: " + SimpleClient.getClient().getActiveUser().getUsername());
//                updateMenuBtn.setVisible(true);  // Show Update button if user is a DIETITIAN
//            } else {
//                updateMenuBtn.setVisible(false);  // Hide Update button if user is not a DIETITIAN
//            }
//        } else {
//            // If not logged in, show login button and hide logout button
//            logoutBtn.setVisible(false);
//            loginBtn.setVisible(true);
//            updateMenuBtn.setVisible(false); // Hide Update button if not logged in
//        }
    }

    private void getUserAuthorizedTools()
    {
        EmployeeType employeeType=SimpleClient.getClient().getActiveUser().getEmployeeType();
        switch (employeeType)
        {
            case DIETITIAN :
                updateMenuBtn.setVisible(true);
                break;
            case COMPANY_MANAGER:
                givePermitBtn.setVisible(true);
                complaintsTableBtn.setVisible(true);
                break;
            case CUSTOMER_SERVICE:
                complaintsTableBtn.setVisible(true);
                break;
            case CUSTOMER_SERVICE_MANAGER:
                complaintsTableBtn.setVisible(true);
                break;
        }
    }


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
        Branch branch = event.getBranch();
        if (branch == null) {
            System.out.println("[SideBarPrimaryBoundary - onBranchSelectedEvent] branch is null");
        }
        openBranchPage(branch);
    }


    private void openBranchPage(Branch branch) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Branch.fxml"));
            Parent branchPageRoot = loader.load();
            BranchPageBoundary controller = loader.getController();
            controller.setBranch(branch);
            while (!controller.branchIsSet)
            {
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

    public void goToSubCompPage(ActionEvent actionEvent) {
        switchScreen("SubComplaint");
    }
    public void viewComplaints(ActionEvent actionEvent) {
        openComplaintsTablePage();
    }
    public void openComplaintsTablePage() {
        switchScreen("Complaints");
    }
    public void onExit()
    {
        EventBus.getDefault().unregister(this);
    }
}
