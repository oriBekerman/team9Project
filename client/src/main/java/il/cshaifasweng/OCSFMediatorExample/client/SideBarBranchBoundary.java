package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchListSentEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchSelectedEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchTablesReceivedEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.UserLoginSuccessEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import il.cshaifasweng.OCSFMediatorExample.entities.Delivery;
import il.cshaifasweng.OCSFMediatorExample.entities.EmployeeType;
import il.cshaifasweng.OCSFMediatorExample.entities.RestTable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static il.cshaifasweng.OCSFMediatorExample.client.App.*;

public class SideBarBranchBoundary {
//public class SideBarBranchBoundary extends AbstractSideBarBoundary {
    private static SideBarBranchBoundary instance;
    public Branch branch;

    private final int instanceId = (int)(Math.random() * 100000);


    private boolean registered = false;
    private boolean isMenuLoaded = false;
    public boolean branchIsSet = false;
    private boolean branchTablesSet = false;
    private Delivery currentDelivery= new Delivery();

    private SecondaryBoundary secondaryBoundary;
    private TableView<il.cshaifasweng.OCSFMediatorExample.entities.MenuItem> menuTableView;

    private Popup popup = new Popup();
    public List<Branch> branchList = null;
    public boolean branchListInit = false;
    private final Object lock = new Object();

    private Runnable popupActionAfterBranchesLoaded = null;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ImageView MOMSImage;

    @FXML
    private Button complaintBtn;

    @FXML
    private Button deliveryBtn;

    @FXML
    private Button menuBtn;

    @FXML
    private Button reservationBtn;

    @FXML
    private VBox sideBar;

    @FXML
    private Button toggleButtonReports;

    @FXML
    private Button tableBtn;


    @FXML
    private Button toggleButtonBranch;


    @FXML
    void navToComplaintPage(ActionEvent event) {
        switchScreen("SubComplaint");
    }

    @FXML
    void navToReservationPage(ActionEvent event) {
        switchScreen("ReservationCnt");
    }

    @FXML
    void navToDeliveryPage(ActionEvent event) {
        currentDelivery.setBranch(branch); //FROM BranchPageBoundary -navToDeliveryPage
        switchToDelivery(currentDelivery); //FROM BranchPageBoundary -navToDeliveryPage
    }


    @FXML
    public void navToMenu(ActionEvent actionEvent) {
//        switchToBranchMenu(branch); THAT WAS FROM THE BranchPageBoundary from "displayMenu"
        onExit();
        switchScreen("secondary");
        try {
            App.setRoot("secondary");
//            SimpleClient.getClient().displayBranchMenu(branch);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @FXML
//    public void navToMenu(ActionEvent actionEvent) {
//        onExit();
//        switchScreen("secondary");
//        try
//        {
//            App.setRoot("secondary");
//        }
//        catch (IOException e)
//        {
//            throw new RuntimeException(e);
//        }
//    }


    @FXML
    void initialize() {
        assert MOMSImage != null : "fx:id=\"MOMSImage\" was not injected: check your FXML file 'sideBarBranch.fxml'.";
        assert complaintBtn != null : "fx:id=\"complaintBtn\" was not injected: check your FXML file 'sideBarBranch.fxml'.";
        assert deliveryBtn != null : "fx:id=\"deliveryBtn\" was not injected: check your FXML file 'sideBarBranch.fxml'.";
        assert menuBtn != null : "fx:id=\"menuBtn\" was not injected: check your FXML file 'sideBarBranch.fxml'.";
        assert reservationBtn != null : "fx:id=\"reservationBtn\" was not injected: check your FXML file 'sideBarBranch.fxml'.";
        assert sideBar != null : "fx:id=\"sideBar\" was not injected: check your FXML file 'sideBarBranch.fxml'.";
        assert toggleButtonBranch != null : "fx:id=\"toggleButtonBranch\" was not injected: check your FXML file 'sideBarBranch.fxml'.";
        assert toggleButtonReports != null : "fx:id=\"toggleReportsButton\" was not injected: check your FXML file 'sideBarBranch.fxml'.";

        instance = this;
        System.out.println("🆔 [SideBarBranchBoundary - initialize] init instanceId = " + instanceId);

        if (registered) {
            System.out.println("⚠️ [SideBarBranchBoundary] Already registered — re-registering...");
            EventBus.getDefault().unregister(this);
        }

        EventBus.getDefault().register(this);
        registered = true;


        updateReportsButtonVisibility();
        toggleButtonReports.setOnAction(this::showReportOptions);


        toggleButtonBranch.setOnAction(e -> {
            System.out.println("[SideBarBranchBoundary- initialize] Button clicked - showing branch list popup");
            GetBranchListPopup();
        });


        // This section display the image of mamasKitchen
        String imagePath = "il/cshaifasweng/OCSFMediatorExample/client/mamasKitchen.jpg";
        Image image = new Image(imagePath);
        MOMSImage.setImage(image);
    }


    public void loadBranchMap(ActionEvent actionEvent){
        openBranchMap();
    }

    public void openBranchMap()
    {
        System.out.println("in open mao in branch boundary");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TableMapPage.fxml"));
            Parent mapPageRoot = loader.load();
            // Get the controller and pass the branch
            TableMapBoundary boundary = loader.getController();
            boundary.setMap(branch);
            synchronized (boundary)
            {
                while (!boundary.mapIsSet)
                {
                    System.out.println("Waiting for map to be set...");
                    boundary.wait();  // Waits until notifyAll() is called
                }
            }
            Platform.runLater(() -> {
                try {
                    App.setContent(mapPageRoot);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }


//    public void openBranchMap() {
//        System.out.println("in open map in branch boundary");
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("TableMapPage.fxml"));
//            Parent mapPageRoot = loader.load();
//            TableMapBoundary boundary = loader.getController();
//            boundary.setMap(branch);  // Triggers map loading
//
//            // Start a background thread to wait safely
//            new Thread(() -> {
//                synchronized (boundary) {
//                    while (!boundary.mapIsSet) {
//                        try {
//                            System.out.println("Waiting for map to be set...");
//                            boundary.wait();  // Wait until the map is ready
//                        } catch (InterruptedException e) {
//                            Thread.currentThread().interrupt();
//                            return;
//                        }
//                    }
//                }
//
//                // Switch to map view on the JavaFX Application Thread
//                Platform.runLater(() -> {
//                    try {
//                        App.setContent(mapPageRoot);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                });
//            }).start();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    public void checkTablesList()
    {
        if(branch.getTables() != null)
        {
            System.out.println("branch tables null");
        }
        if (branch.getTables().isEmpty())
        {
            System.out.println("branch tables empty");
        }
    }

    @Subscribe
    public void onBranchTablesEvent(BranchTablesReceivedEvent event) {
        synchronized (lock) {
            System.out.println("Tables received for branch: " + branch.getName());
            Set<RestTable> tables = event.getTables();
            List<RestTable> newTables=tables.stream().toList();

            if (tables != null && !tables.isEmpty()) {
                branch.setRestTables(newTables);
                branchTablesSet = true;
                lock.notifyAll(); // Wake up any waiting threads
            } else {
                System.out.println("Received empty table list for branch!");
            }
        }
    }


//    //get list of brunches pop up
//    private void GetBranchListPopup() {
//        synchronized (lock) {
//            if (!branchListInit) {
//                try {
//                    SimpleClient.getClient().getBranchList();
//                    while (!branchListInit) {
//                        lock.wait();
//                    }
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    System.out.println("Thread interrupted while waiting for branch list.");
//                    return;
//                }
//            }
//        }
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("BranchList.fxml"));
//            Parent popupContent = loader.load();
//
//            // Get BranchListController instance and set branches
//            BranchListBoundary controller = loader.getController();
//            controller.setBranches(branchList);
//
//            popup.getContent().clear();
//            popup.getContent().add(popupContent);
//            popup.setAutoHide(true);
//            // Ensure popup shows correctly
//            if (toggleButtonBranch.getScene() != null) {
//                popup.show(toggleButtonBranch.getScene().getWindow(),
//                        toggleButtonBranch.localToScreen(0, 0).getX(),
//                        toggleButtonBranch.localToScreen(0, 0).getY() + toggleButtonBranch.getHeight());
//            } else {
//                System.out.println("toggleButtonBranch scene is NULL - cannot display popup");
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }




//private void showBranchListPopup() {
//    Platform.runLater(() -> {
//        System.out.println("📦 [SideBarBranchBoundary- showBranchListPopup] Loading BranchList.fxml...");
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("BranchList.fxml"));
//            Parent popupContent = loader.load();
//            System.out.println("✅ [SideBarBranchBoundary- showBranchListPopup] FXML loaded successfully");
//
//            BranchListBoundary controller = loader.getController();
//            controller.setBranches(branchList);
//            System.out.println("📋 [SideBarBranchBoundary- showBranchListPopup] Controller and data set");
//
//            popup.getContent().clear();
//            popup.getContent().add(popupContent);
//            popup.setAutoHide(true);
//
//            if (toggleButtonBranch.getScene() != null) {
//                popup.show(toggleButtonBranch.getScene().getWindow(),
//                        toggleButtonBranch.localToScreen(0, 0).getX(),
//                        toggleButtonBranch.localToScreen(0, 0).getY() + toggleButtonBranch.getHeight());
//                System.out.println("🎉 [SideBarBranchBoundary- showBranchListPopup] Popup shown");
//            } else {
//                System.out.println("❌ [SideBarBranchBoundary- showBranchListPopup] toggleButtonBranch.getScene() is NULL");
//            }
//        } catch (IOException e) {
//            System.err.println("❌ [SideBarBranchBoundary- showBranchListPopup] Failed to load popup FXML");
//            e.printStackTrace();
//        }
//    });
//    System.out.println("✅ [" + getClass().getSimpleName() + "] Done with showBranchListPopup for instanceId = " + instanceId);
//}


    private void GetBranchListPopup() {
        System.out.println("🟡 [" + getClass().getSimpleName() + "] Requesting popup via BranchListManager");

        Runnable popupAction = () -> {
            System.out.println("hereee---------------------------");
            List<Branch> branches = branchList;
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




//private void showBranchListPopup(List<Branch> branches) {
//    System.out.println("👀 [SideBarBranchBoundary -showBranchListPopup] Branch list size in popup = " + branches.size());
//
//    Platform.runLater(() -> {
//        System.out.println("📦 [" + getClass().getSimpleName() + "] Showing popup...");
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("BranchList.fxml"));
//            Parent popupContent = loader.load();
//            BranchListBoundary controller = loader.getController();
//            controller.setBranches(branches);
//            popup.getContent().clear();
//            popup.getContent().add(popupContent);
//            popup.setAutoHide(true);
//            if (toggleButtonBranch.getScene() != null) {
//                double x = toggleButtonBranch.localToScreen(0, 0).getX();
//                double y = toggleButtonBranch.localToScreen(0, 0).getY();
//                System.out.println("📍 Button position: (" + x + ", " + y + ")");
//                popup.show(toggleButtonBranch.getScene().getWindow(), x, y + toggleButtonBranch.getHeight());
//                System.out.println("🎉 Popup shown");
//            } else {
//                System.out.println("❌ [showBranchListPopup] toggleButtonBranch.getScene() is NULL!!! Popup CANNOT appear.");
//                System.out.println("🔎 ToggleButton ID: " + toggleButtonBranch.getId());
//                System.out.println("🧩 toggleButtonBranch is visible: " + toggleButtonBranch.isVisible());
//                System.out.println("🧩 toggleButtonBranch is managed: " + toggleButtonBranch.isManaged());
//                System.out.println("🧩 toggleButtonBranch parent: " + toggleButtonBranch.getParent());
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    });
//}


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
        System.out.println("[SideBarBranchBoundary] getPopup() triggered");
        GetBranchListPopup();
    }

    @Subscribe
    public void onBranchSelectedEvent(BranchSelectedEvent event) {
        this.branch = event.getBranch();
        System.out.println("📌[SideBarBranchBoundary -onBranchSelectedEvent] Branch selected event received: " + branch.getName() +
                " (ID=" + branch.getId() + ")");
        NavigationHelper.openBranchPage(branch, this::getUserAuthorizedTools);
        updateReportsButtonVisibility();
    }


//    //open selected branch page
//    private void openBranchPage(Branch branch) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("Branch.fxml"));
//            Parent branchPageRoot = loader.load();
//            BranchPageBoundary controller = loader.getController();
//            controller.setBranch(branch);
//            getUserAuthorizedTools();
//
//            while (!controller.branchIsSet)
//            {
//                System.out.println("Waiting for branch to be set");
//            }
//            App.setContent(branchPageRoot);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void getUserAuthorizedTools() {
        if (SimpleClient.getClient().getActiveUser() != null) {
            tableBtn.setVisible(true);
        }
        else {
            tableBtn.setVisible(false);
        }
    }


//    //handle branch list sent
//    @Subscribe
//    public void onBranchListSentEvent(BranchListSentEvent event) {
//        synchronized (lock) {
//            this.branchList = event.branches;
//            this.branchListInit = true;
//            lock.notifyAll(); // Notify waiting threads that branches are initialized
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




    @Subscribe
    public void onLoginSuccess(UserLoginSuccessEvent event) {
        // Assuming that the role is part of the authorization or similar mapping
        EmployeeType userRole = SimpleClient.getClient().getActiveUser().getEmployeeType();
        toggleButtonReports.setVisible(userRole == EmployeeType.COMPANY_MANAGER);
        toggleButtonReports.setVisible(userRole == EmployeeType.BRANCH_MANAGER);
    }


    @FXML
    void showReportOptions(ActionEvent event) {
        if (branch == null) {
            System.out.println("[SideBarBranchBoundary -showReportOptions ] ERROR: Branch is not set when Reports button clicked.");
            return;
        }
        System.out.println("[SideBarBranchBoundary-showReportOptions] Reports button clicked. Current branch: " + branch.getName());

        ContextMenu contextMenu = new ContextMenu();
        MenuItem resItem = new MenuItem("Reservations");
        resItem.setOnAction(e -> openReportPage("Reservations"));
        MenuItem delItem = new MenuItem("Deliveries");
        delItem.setOnAction(e -> openReportPage("Deliveries"));
        MenuItem compItem = new MenuItem("Complaints");
        compItem.setOnAction(e -> openReportPage("Complaints"));

        contextMenu.getItems().addAll(resItem, delItem, compItem);
        contextMenu.show(toggleButtonReports, Side.RIGHT, 0, 0);
    }


    // Opens the report page and sets the required data
    private void openReportPage(String reportType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("report.fxml"));
            Parent reportRoot = loader.load();
            ReportBoundary controller = loader.getController();
            controller.setBranch(this.branch); // explicitly set branch
            controller.setReportTitle(reportType);
            controller.displayReport(reportType); // trigger report request explicitly
            App.setContent(reportRoot); // Display the loaded report page
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void updateReportsButtonVisibility() {
        ActiveUser currentUser = SimpleClient.getClient().getActiveUser();
        if (currentUser != null) {
            EmployeeType role = currentUser.getEmployeeType();
            toggleButtonReports.setVisible(
                    role == EmployeeType.COMPANY_MANAGER ||
                            role == EmployeeType.BRANCH_MANAGER
            );
        } else {
            toggleButtonReports.setVisible(false);
        }
    }

    public SideBarBranchBoundary() {
        instance = this;
    }

    public static SideBarBranchBoundary getInstance() {
        return instance;
    }


    public void setBranch(Branch branch) {
        this.branch = branch;
        System.out.println("[SideBarBranchBoundary] Branch explicitly set: " + branch.getName());
    }
//
//    public void onExit()
//    {
//        EventBus.getDefault().unregister(this);
//    }

    public void onExit() {
        System.out.println("[SideBarBranchBoundary- onExit] Called");
        EventBus.getDefault().unregister(this);
        registered = false; // 🔧 ADD THIS LINE
        System.out.println("[SideBarBranchBoundary- onExit] Unregistered from EventBus");
        resetBranchListState();
//        BranchListManager.getInstance().reset(); // optional: clears cached list
    }


    public void resetBranchListState() {
        synchronized (lock) {
            branchList = null;
            branchListInit = false;
            System.out.println("[SideBarBranchBoundary- resetBranchListState] Reset state");
        }
    }
}

// original version with some additional prints
//    //get list of brunches pop up
//    private void GetBranchListPopup() {
//        synchronized (lock) {
//            if (!branchListInit) {
//                try {
//                    SimpleClient.getClient().getBranchList();
//                    while (!branchListInit) {
//                        lock.wait();
//                    }
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    System.out.println("Thread interrupted while waiting for branch list.");
//                    return;
//                }
//            }
//        }
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("BranchList.fxml"));
//            Parent popupContent = loader.load();
//            // Get BranchListController instance and set branches
//            BranchListBoundary controller = loader.getController();
//            controller.setBranches(branchList);
//
//            popup.getContent().clear();
//            popup.getContent().add(popupContent);
//            popup.setAutoHide(true);
//            // Ensure popup shows correctly
//            if (toggleButtonBranch.getScene() != null) {
//                popup.show(toggleButtonBranch.getScene().getWindow(),
//                        toggleButtonBranch.localToScreen(0, 0).getX(),
//                        toggleButtonBranch.localToScreen(0, 0).getY() + toggleButtonBranch.getHeight());
//            } else {
//                System.out.println("toggleButtonBranch scene is NULL - cannot display popup");
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    // Get list of branches popup
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