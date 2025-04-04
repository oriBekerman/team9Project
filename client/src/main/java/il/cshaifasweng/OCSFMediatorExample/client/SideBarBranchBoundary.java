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
    private static SideBarBranchBoundary instance;
    public Branch branch;

    private boolean isMenuLoaded = false;
    public boolean branchIsSet = false;
    private boolean branchTablesSet = false;
    private Delivery currentDelivery= new Delivery();
    private SecondaryBoundary secondaryBoundary;
    private TableView<il.cshaifasweng.OCSFMediatorExample.entities.MenuItem> menuTableView;


    public void BranchPageBoundary()
    {
        if (!EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().register(this);
        }
    }

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
    private Popup popup = new Popup();

    public List<Branch> branchList = null;
    public boolean branchListInit = false;
    private final Object lock = new Object();

    @FXML
    void navToComplaintPage(ActionEvent event) {
        switchScreen("SubComplaint");
    }


    @FXML
    void navToDeliveryPage(ActionEvent event) {
        currentDelivery.setBranch(branch); //FROM BranchPageBoundary -navToDeliveryPage
        switchToDelivery(currentDelivery); //FROM BranchPageBoundary -navToDeliveryPage
    }

    @FXML
    void navToReservationPage(ActionEvent event) {
        switchScreen("ReservationCnt");
    }

    @FXML
    public void navToMenu(ActionEvent actionEvent)
    {
        onExit();
        App.switchToBranchMenu(branch);
        try
        {
            App.setRoot("secondary");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

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

        EventBus.getDefault().register(this);
        SimpleClient.getClient().getBranchList(); // Request branch list from server
        updateReportsButtonVisibility();
        toggleButtonReports.setOnAction(this::showReportOptions);

        toggleButtonBranch.setOnAction(e -> {
            System.out.println("[SideBarBranchBoundary- initialize] Button clicked - showing branch list popup");
            GetBranchListPopup();
        });

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

    @Subscribe
    public void onBranchSelectedEvent(BranchSelectedEvent event) {
        this.branch = event.getBranch();
        System.out.println("[SideBarBranchBoundary - onBranchSelectedEvent] Branch selected event received: " + branch.getName() +
                " (ID=" + branch.getId() + ")");
        openBranchPage(this.branch);
        updateReportsButtonVisibility();
    }

    private void openBranchPage(Branch branch) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Branch.fxml"));
            Parent branchPageRoot = loader.load();
            BranchPageBoundary controller = loader.getController();
            controller.setBranch(branch);
            getUserAuthorizedTools();

            while (!controller.branchIsSet)
            {
                System.out.println("Waiting for branch to be set");
            }
            App.setContent(branchPageRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    public void updateSidebarForUser() {
//        if (SimpleClient.getClient().getActiveUser() != null) {
//            tableBtn.setVisible(true);
//        } else {
//            tableBtn.setVisible(false);
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

    @Subscribe
    public void onBranchListSentEvent(BranchListSentEvent event) {
        synchronized (lock) {
            this.branchList = event.branches;
            this.branchListInit = true;
            System.out.println("[SideBarBranchBoundary] onBranchesSentEvent");
            lock.notifyAll(); // Notify waiting threads that branches are initialized
        }
    }

    @Subscribe
    public void onLoginSuccess(UserLoginSuccessEvent event)
    {
        EmployeeType userRole = SimpleClient.getClient().getActiveUser().getEmployeeType();
        toggleButtonReports.setVisible(userRole == EmployeeType.COMPANY_MANAGER);
        toggleButtonReports.setVisible(userRole == EmployeeType.BRANCH_MANAGER);
    }

    @FXML
    void showReportOptions(ActionEvent event)
    {
        if (branch == null) {
            System.out.println("[SideBarBranchBoundary] ERROR: Branch is not set when Reports button clicked.");
            return;
        }
        System.out.println("[SideBarBranchBoundary] Reports button clicked. Current branch: " + branch.getName());

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

    public void onExit()
    {
        EventBus.getDefault().unregister(this);
    }
}
