package il.cshaifasweng.OCSFMediatorExample.client;
import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchListSentEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchSelectedEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.EmployeeType;
import javafx.application.Platform;
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
import static il.cshaifasweng.OCSFMediatorExample.client.App.switchToDelivery;

public class SideBarPrimaryBoundary {

    private final int instanceId = (int)(Math.random() * 100000);

    private static SideBarPrimaryBoundary instance;


    private Popup popup = new Popup();
    public List<Branch> branchList = null;
    public boolean branchListInit = false;
    private final Object lock = new Object();

    private boolean registered = false;
    public Branch branch;


    private Runnable popupActionAfterBranchesLoaded = null;


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
            showErrorMessage("[SideBarPrimaryBoundary- givePermit] Network error occurred while granting the permit.");
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

        instance = this;
        System.out.println("🆔 [SideBarPrimaryBoundary - initialize ] init instanceId = " + instanceId);


        if (registered) {
            System.out.println("⚠️ [SideBarPrimaryBoundary] Already registered — re-registering...");
            EventBus.getDefault().unregister(this);
        }

        EventBus.getDefault().register(this);
        registered = true;


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


//    private void GetBranchListPopup() {
//        System.out.println("🚀 [SideBarPrimaryBoundary - GetBranchListPopup] Entered");
//        System.out.println("🚀 [ SideBarPrimaryBoundary- GetBranchListPopup] instanceId = " + instanceId);
//
//        popupActionAfterBranchesLoaded = this::showBranchListPopup;  // Save action
//
//        synchronized (lock) {
//            if (!branchListInit) {
//                System.out.println("🟡 [SideBarPrimaryBoundary- GetBranchListPopup] branchListInit is FALSE → sending request");
//                SimpleClient.getClient().getBranchList();  // async call
//                return; // Exit now — the popup will show when event arrives
//            }
//        }
//        showBranchListPopup(); // If already initialized, just show it now
//    }


//    private void showBranchListPopup() {
//        Platform.runLater(() -> {
//            System.out.println("📦 [SideBarPrimaryBoundary- showBranchListPopup] Loading BranchList.fxml...");
//            try {
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("BranchList.fxml"));
//                Parent popupContent = loader.load();
//                System.out.println("✅ [SideBarPrimaryBoundary- showBranchListPopup] FXML loaded successfully");
//
//                BranchListBoundary controller = loader.getController();
//                controller.setBranches(branchList);
//                System.out.println("📋 [SideBarPrimaryBoundary- showBranchListPopup] Controller and data set");
//
//                popup.getContent().clear();
//                popup.getContent().add(popupContent);
//                popup.setAutoHide(true);
//
//                if (toggleButtonBranch.getScene() != null) {
//                    popup.show(toggleButtonBranch.getScene().getWindow(),
//                            toggleButtonBranch.localToScreen(0, 0).getX(),
//                            toggleButtonBranch.localToScreen(0, 0).getY() + toggleButtonBranch.getHeight());
//                    System.out.println("🎉 [SideBarPrimaryBoundary- showBranchListPopup] Popup shown");
//                } else {
//                    System.out.println("❌ [SideBarPrimaryBoundary- showBranchListPopup] toggleButtonBranch.getScene() is NULL");
//                }
//            } catch (IOException e) {
//                System.err.println("❌ [SideBarPrimaryBoundary- showBranchListPopup] Failed to load popup FXML");
//                e.printStackTrace();
//            }
//        });
//        System.out.println("✅ [" + getClass().getSimpleName() + "] Done with showBranchListPopup for instanceId = " + instanceId);
//    }


    private void GetBranchListPopup() {
        System.out.println("🟡 [" + getClass().getSimpleName() + "] Requesting popup via BranchListManager");

        Runnable popupAction = () ->
        {
            System.out.println("HERE-----------------------");
            List<Branch> branches = branchList;
            System.out.println("[SideBarPrimaryBoundary - GetBranchListPopup]  the branches are:" + branchList.stream().toList().toString());
            if (branches == null || branches.isEmpty()) {
                System.out.println("❌ [" + getClass().getSimpleName() + "] No branches available in callback!");
                return;
            }

            System.out.println("✅ [" + getClass().getSimpleName() + "] Callback executed, showing popup.");
            Platform.runLater(() -> showBranchListPopup(branches));
        };

        BranchListManager.getInstance().requestBranchList(popupAction);
    }


    @Subscribe
    public void onBranchListSentEvent(BranchListSentEvent event)
    {
        this.branchList = event.branches;
    }


//    private void showBranchListPopup(List<Branch> branches) {
//        System.out.println("👀 [SideBarPrimaryBoundary - showBranchListPopup] Branch list size in popup = " + branches.size());
//
//        Platform.runLater(() -> {
//            System.out.println("📦 [" + getClass().getSimpleName() + "] Showing popup...");
//            try {
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("BranchList.fxml"));
//                Parent popupContent = loader.load();
//                BranchListBoundary controller = loader.getController();
//                controller.setBranches(branches);
//                popup.getContent().clear();
//                popup.getContent().add(popupContent);
//                popup.setAutoHide(true);
//                if (toggleButtonBranch.getScene() != null) {
//                    double x = toggleButtonBranch.localToScreen(0, 0).getX();
//                    double y = toggleButtonBranch.localToScreen(0, 0).getY();
//                    System.out.println("📍 Button position: (" + x + ", " + y + ")");
//                    popup.show(toggleButtonBranch.getScene().getWindow(), x, y + toggleButtonBranch.getHeight());
//                    System.out.println("🎉 Popup shown");
//                } else {
//                    System.out.println("❌ [SideBarPrimaryBoundary - showBranchListPopup] toggleButtonBranch.getScene() is NULL!!! Popup CANNOT appear.");
//                    System.out.println("🔎 ToggleButton ID: " + toggleButtonBranch.getId());
//                    System.out.println("🧩 toggleButtonBranch is visible: " + toggleButtonBranch.isVisible());
//                    System.out.println("🧩 toggleButtonBranch is managed: " + toggleButtonBranch.isManaged());
//                    System.out.println("🧩 toggleButtonBranch parent: " + toggleButtonBranch.getParent());
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }


    private void showBranchListPopup(List<Branch> branches) {
        try {
            System.out.println("📦 [showBranchListPopup] Loading BranchList.fxml...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BranchList.fxml"));
            Parent popupContent = loader.load();

            BranchListBoundary controller = loader.getController();
            controller.setBranches(branches);
            System.out.println("📋 [showBranchListPopup] Controller set with branches");

            popup.getContent().clear();
            popup.getContent().add(popupContent);
            popup.setAutoHide(true);

            if (toggleButtonBranch.getScene() != null) {
                popup.show(
                        toggleButtonBranch.getScene().getWindow(),
                        toggleButtonBranch.localToScreen(0, 0).getX(),
                        toggleButtonBranch.localToScreen(0, 0).getY() + toggleButtonBranch.getHeight()
                );
                System.out.println("🎉 [showBranchListPopup] Popup shown!");
            } else {
                System.out.println("❌ [showBranchListPopup] toggleButtonBranch.getScene() is null");
            }
        } catch (IOException e) {
            System.err.println("❌ [showBranchListPopup] Failed to load BranchList.fxml");
            e.printStackTrace();
        }
    }





    @FXML
    public void getPopup(ActionEvent actionEvent) {
        System.out.println("[SideBarPrimaryBoundary] getPopup() triggered");
        GetBranchListPopup();
    }

    // Handle the branch selected from the list
    @Subscribe
    public void onBranchSelectedEvent(BranchSelectedEvent event) {
        Branch branch = event.getBranch();
        this.branch = branch;
        System.out.println("📌 [SideBarPrimaryBoundary- onBranchSelectedEvent] Branch selected: " + branch.getName());

        if (branch == null) {
            System.out.println("[SideBarPrimaryBoundary - onBranchSelectedEvent] branch is null");
            return;
        }
        NavigationHelper.openBranchPage(branch, null);
    }


//    private void openBranchPage(Branch branch) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("Branch.fxml"));
//            Parent branchPageRoot = loader.load();
//            BranchPageBoundary controller = loader.getController();
//            controller.setBranch(branch);
//            while (!controller.branchIsSet)
//            {
//                System.out.println("Waiting for branch to be set");
//            }
//            App.setContent(branchPageRoot);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


//    //handle branch list sent
//    @Subscribe
//    public void onBranchListSentEvent(BranchListSentEvent event) {
//        synchronized (lock) {
//            this.branchList = event.branches;
//            this.branchListInit = true;
//            lock.notifyAll(); // Notify waiting threads that branches are initialized
//
//        }
//    }


//    @Subscribe
//    public void onBranchListSentEvent(BranchListSentEvent event) {
//        System.out.println("📬 [" + getClass().getSimpleName() + "- onBranchListSentEvent] Received branch list (" + event.branches.size() + ")");
//        System.out.println("💡 [" + getClass().getSimpleName() + "- onBranchListSentEvent] This handler is ACTIVE!");
//        System.out.println("🆔 [" + getClass().getSimpleName() + "- onBranchListSentEvent] instanceId = " + instanceId);
//        System.out.println("📬 [BranchListManager] Received branch list with " + event.branches.size() + " items");
//
//
//        synchronized (lock) {
//            this.branchList = event.branches;
//            this.branchListInit = true;
//            lock.notifyAll(); // Notify any background threads waiting
//
//            if (popupActionAfterBranchesLoaded != null) {
//                Runnable action = popupActionAfterBranchesLoaded;
//                popupActionAfterBranchesLoaded = null;
//
//                System.out.println("🧠 [" + getClass().getSimpleName() + "] Running deferred popup action on instanceId = " + instanceId);
//
//                Platform.runLater(() -> {
//                    System.out.println("🔍 [" + getClass().getSimpleName() + "] Executing popup on UI thread for instanceId = " + instanceId);
//                    try {
//                        action.run();
//                        System.out.println("✅ [" + getClass().getSimpleName() + "] Popup action executed successfully for instanceId = " + instanceId);
//                    } catch (Exception e) {
//                        System.err.println("❌ [" + getClass().getSimpleName() + "] Error while executing popup action for instanceId = " + instanceId);
//                        e.printStackTrace();
//                    }
//                });
//
//            } else {
//                System.out.println("⚠️ [" + getClass().getSimpleName() + "] No popup action pending on instanceId = " + instanceId);
//            }
//        }
//    }


    public void goToSubCompPage(ActionEvent actionEvent) {
        switchScreen("SubComplaint");
    }
    public void viewComplaints(ActionEvent actionEvent) {
        openComplaintsTablePage();
    }
    public void openComplaintsTablePage() {
        switchScreen("Complaints");
    }
//    public void onExit()
//    {
//        EventBus.getDefault().unregister(this);
//    }

    public void onExit() {
        System.out.println("[SideBarPrimaryBoundary- onExit] Called");
        EventBus.getDefault().unregister(this);
        registered = false; // 🔧 ADD THIS LINE
        System.out.println("SideBarPrimaryBoundary- onExit] Unregistered from EventBus");
        resetBranchListState();
    }

    public void resetBranchListState() {
        synchronized (lock) {
            branchList = null;
            branchListInit = false;
            System.out.println("[SideBarPrimaryBoundary- resetBranchListState] Reset state");
        }
    }


    public static SideBarPrimaryBoundary getInstance() {
        return instance;
    }

}

// original version with some additional prints
//    //get list of brunches pop up
//    private void GetBranchListPopup() {
//        System.out.println("🚀 [GetBranchListPopup] Entered");
//
//        synchronized (lock) {
//            if (!branchListInit) {
//                System.out.println("🟡 [GetBranchListPopup] branchListInit is FALSE → sending request");
//                try {
//                    SimpleClient.getClient().getBranchList();
//                    while (!branchListInit) {
//                        System.out.println("🔄 [GetBranchListPopup] Waiting for branch list...");
//                        lock.wait();
//                    }
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    System.out.println("❌ [GetBranchListPopup] Interrupted while waiting");
//                    return;
//                }
//            } else {
//            System.out.println("🟢 [GetBranchListPopup] branchListInit is TRUE → using cached list");
//            }
//        }
//        System.out.println("📦 [GetBranchListPopup] Loading BranchList.fxml...");
//
//        try {
//            System.out.println("🟢 [GetBranchListPopup] About to create new popup");
//
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("BranchList.fxml"));
//            Parent popupContent = loader.load();
//            System.out.println("✅ [GetBranchListPopup] FXML loaded successfully");
//
//            // Get BranchListController instance and set branches
//            BranchListBoundary controller = loader.getController();
//            controller.setBranches(branchList);
//            System.out.println("📋 [GetBranchListPopup] Controller and data set");
//
//
//            popup.getContent().clear();
//            popup.getContent().add(popupContent);
//            popup.setAutoHide(true);
//            // Ensure popup shows correctly
//            if (toggleButtonBranch.getScene() != null) {
//                popup.show(toggleButtonBranch.getScene().getWindow(),
//                        toggleButtonBranch.localToScreen(0, 0).getX(),
//                        toggleButtonBranch.localToScreen(0, 0).getY() + toggleButtonBranch.getHeight());
//                System.out.println("🎉 [GetBranchListPopup] Popup shown");
//            } else {
//                System.out.println("❌ [GetBranchListPopup] toggleButtonBranch.getScene() is NULL");
//            }
//
//        } catch (IOException e) {
//            System.err.println("❌ [GetBranchListPopup] Failed to load popup FXML");
//            e.printStackTrace();
//        }
//    }

// Get list of branches popup
//    private void GetBranchListPopup() {
//        System.out.println("🚀 [GetBranchListPopup] Entered");
//
//        synchronized (lock) {
//            if (!branchListInit) {
//                System.out.println("🟡 [GetBranchListPopup] branchListInit is FALSE → sending request");
//
//                SimpleClient.getClient().getBranchList();
//
//                // Wait in a background thread
//                new Thread(() -> {
//                    synchronized (lock) {
//                        while (!branchListInit) {
//                            System.out.println("🔄 [GetBranchListPopup] Waiting for branch list...");
//                            try {
//                                System.out.println("🔍 [GetBranchListPopup] this line is before lock.wait,  branchListInit = " + branchListInit);
//                                lock.wait();  // Wait OFF the UI thread!
//                            } catch (InterruptedException e) {
//                                Thread.currentThread().interrupt();
//                                System.out.println("❌ [GetBranchListPopup] Interrupted");
//                                return;
//                            }
//                        }
//                    }
//
//                    // When data is ready → load popup on UI thread
//                    Platform.runLater(() -> {
//                        System.out.println("📦 [GetBranchListPopup] Loading BranchList.fxml...");
//                        try {
//                            FXMLLoader loader = new FXMLLoader(getClass().getResource("BranchList.fxml"));
//                            Parent popupContent = loader.load();
//                            System.out.println("✅ [GetBranchListPopup] FXML loaded successfully");
//
//                            BranchListBoundary controller = loader.getController();
//                            controller.setBranches(branchList);
//                            System.out.println("📋 [GetBranchListPopup] Controller and data set");
//
//                            popup.getContent().clear();
//                            popup.getContent().add(popupContent);
//                            popup.setAutoHide(true);
//
//                            if (toggleButtonBranch.getScene() != null) {
//                                popup.show(toggleButtonBranch.getScene().getWindow(),
//                                        toggleButtonBranch.localToScreen(0, 0).getX(),
//                                        toggleButtonBranch.localToScreen(0, 0).getY() + toggleButtonBranch.getHeight());
//                                System.out.println("🎉 [GetBranchListPopup] Popup shown");
//                            } else {
//                                System.out.println("❌ [GetBranchListPopup] toggleButtonBranch.getScene() is NULL");
//                            }
//                        } catch (IOException e) {
//                            System.err.println("❌ [GetBranchListPopup] Failed to load popup FXML");
//                            e.printStackTrace();
//                        }
//                    });
//                }).start();
//
//                return;  // Don't continue in the UI thread
//            } else {
//                System.out.println("🟢 [GetBranchListPopup] branchListInit is TRUE → using cached list");
//            }
//        }
//
//        // If already ready → show immediately
//        Platform.runLater(() -> {
//            try {
//                System.out.println("📦 [GetBranchListPopup] Loading BranchList.fxml (cached path)...");
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("BranchList.fxml"));
//                Parent popupContent = loader.load();
//                System.out.println("✅ [GetBranchListPopup] FXML loaded successfully (cached)");
//
//                BranchListBoundary controller = loader.getController();
//                controller.setBranches(branchList);
//                System.out.println("📋 [GetBranchListPopup] Controller and data set (cached)");
//
//                popup.getContent().clear();
//                popup.getContent().add(popupContent);
//                popup.setAutoHide(true);
//
//                if (toggleButtonBranch.getScene() != null) {
//                    popup.show(toggleButtonBranch.getScene().getWindow(),
//                            toggleButtonBranch.localToScreen(0, 0).getX(),
//                            toggleButtonBranch.localToScreen(0, 0).getY() + toggleButtonBranch.getHeight());
//                    System.out.println("🎉 [GetBranchListPopup] Popup shown (cached)");
//                } else {
//                    System.out.println("❌ [GetBranchListPopup] toggleButtonBranch.getScene() is NULL (cached)");
//                }
//            } catch (IOException e) {
//                System.err.println("❌ [GetBranchListPopup] Failed to load popup FXML (cached)");
//                e.printStackTrace();
//            }
//        });
//    }