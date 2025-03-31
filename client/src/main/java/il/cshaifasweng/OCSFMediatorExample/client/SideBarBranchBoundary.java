package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchListSentEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchSelectedEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import il.cshaifasweng.OCSFMediatorExample.entities.Employees.EmployeeType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class SideBarBranchBoundary {
    private static SideBarBranchBoundary instance;
    public Branch branch;

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
        switchScreen("Delivery");
    }

    @FXML
    void navToReservationPage(ActionEvent event) {
        switchScreen("ReservationCnt");
    }

    @FXML
    public void navToMenu(ActionEvent actionEvent) {
        switchScreen("menu");
        try {
            App.setRoot("menu");
            SimpleClient.getClient().displayBranchMenu(branch);
//            Menu menu = new Menu(branch.getBranchMenuItems());
//            menu.printMenu();
//            SimpleClient.getClient().showMenu(menu);
        } catch (Exception e) {
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

        // Set the button action here
        EventBus.getDefault().register(this);
        SimpleClient.getClient().getBranchList(); // Request branch list from server
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

    @Subscribe
    public void onBranchSelectedEvent(BranchSelectedEvent event) {
        this.branch = event.getBranch();
        System.out.println("[SideBarBranchBoundary - onBranchSelectedEvent] Branch selected event received: " + branch.getName() +
                " (ID=" + branch.getId() + ")");
        openBranchPage(this.branch);
        updateReportsButtonVisibility();
    }


    //open selected branch page
    private void openBranchPage(Branch branch) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Branch.fxml"));
            Parent branchPageRoot = loader.load();
            // Get the controller and pass the branch
            BranchPageBoundary controller = loader.getController();
            controller.setBranch(branch);
//            if (controller.branchIsSet) {
//                System.out.println("branch is already set");
//            }
//            while (!controller.branchIsSet) {
//                System.out.println("Waiting for branch to be set");
//            }
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
            System.out.println("[SideBarBranchBoundary] onBranchesSentEvent");
            lock.notifyAll(); // Notify waiting threads that branches are initialized
        }
    }



    @Subscribe
    public void onLoginSuccess(UserLoginSuccessEvent event) {
        // Assuming that the role is part of the authorization or similar mapping
//        EmployeeType userRole = SessionManager.getInstance().getEmployeeType(); // Implement this method in SessionManager
        EmployeeType userRole = SimpleClient.getClient().getActiveUser().getEmployeeType();
        toggleButtonReports.setVisible(userRole == EmployeeType.COMPANY_MANAGER);
        toggleButtonReports.setVisible(userRole == EmployeeType.BRANCH_MANAGER);
    }


    @FXML
    void showReportOptions(ActionEvent event) {
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

}
