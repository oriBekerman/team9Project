//package il.cshaifasweng.OCSFMediatorExample.client;
//
//import java.io.IOException;
//import java.net.URL;
//import java.util.List;
//import java.util.ResourceBundle;
//
//import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchListSentEvent;
//import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchSelectedEvent;
//import il.cshaifasweng.OCSFMediatorExample.client.Events.UserLoginSuccessEvent;
//import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
//import il.cshaifasweng.OCSFMediatorExample.entities.EmployeeType;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.chart.BarChart;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.stage.Popup;
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//
//import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;
//
//public class ReportBoundary {
//
//    @FXML
//    private ResourceBundle resources;
//
//    @FXML
//    private URL location;
//
//    @FXML
//    private Button BackToHPbtn;
//
//    @FXML
//    private Label HomePageLabel;
//
//    @FXML
//    private ImageView MOMSImage;
//
//    @FXML
//    private Button MenuBtn;
//
//    @FXML
//    private Button UpdateMenuBtn;
//
//    @FXML
//    private Label WelcomeLabel;
//
//    @FXML
//    private BarChart<String, Number> complaintsChart;
//
//    @FXML
//    private TableView<ReportDetail> reportTableView;
//
//    @FXML
//    private Button deliveryBtn;
//
//    @FXML
//    private Button loginBtn;
//
//    @FXML
//    private Button logoutBtn;
//
//    @FXML
//    private Button toggleButtonBranch;
//
//    @FXML
//    private Button toggleButtonReports;
//
//    private Popup popup = new Popup();
//
//    public List<Branch> branchList = null;
//    public boolean branchListInit = false;
//
//    private final Object lock = new Object();
//
//
//    @FXML
//    void BackToHPfunc(ActionEvent event) {switchScreen("Home Page");}
//
//    @FXML
//    void navToDelivery(ActionEvent event) {switchScreen("Delivery Page");}
//
//    @FXML
//    void navToLoginP(ActionEvent event) {switchScreen("Login");}
//
//    @FXML
//    void navToMenu(ActionEvent event) {switchScreen("Menu");}
//
//    @FXML
//    void navToUpMenu(ActionEvent event) {switchScreen("Update Menu");}
//
//    @FXML
//    void navToLogOut(ActionEvent event) {
//        switchScreen("LogOut");
//        // Call the logout method in SimpleClient to clear active user
//        SimpleClient.getClient().logout();
//
//        // Hide the logout button and show the login button again
//        logoutBtn.setVisible(false);
//        loginBtn.setVisible(true);
//        // Hide the Update button after logging out
//        UpdateMenuBtn.setVisible(false);
//    }
//
//
//    @FXML
//    void initialize() {
//        assert BackToHPbtn != null : "fx:id=\"BackToHPbtn\" was not injected: check your FXML file 'report.fxml'.";
//        assert HomePageLabel != null : "fx:id=\"HomePageLabel\" was not injected: check your FXML file 'report.fxml'.";
//        assert MOMSImage != null : "fx:id=\"MOMSImage\" was not injected: check your FXML file 'report.fxml'.";
//        assert MenuBtn != null : "fx:id=\"MenuBtn\" was not injected: check your FXML file 'report.fxml'.";
//        assert UpdateMenuBtn != null : "fx:id=\"UpdateMenuBtn\" was not injected: check your FXML file 'report.fxml'.";
//        assert WelcomeLabel != null : "fx:id=\"WelcomeLabel\" was not injected: check your FXML file 'report.fxml'.";
//        assert complaintsChart != null : "fx:id=\"complaintsChart\" was not injected: check your FXML file 'report.fxml'.";
//        assert deliveryBtn != null : "fx:id=\"deliveryBtn\" was not injected: check your FXML file 'report.fxml'.";
//        assert loginBtn != null : "fx:id=\"loginBtn\" was not injected: check your FXML file 'report.fxml'.";
//        assert logoutBtn != null : "fx:id=\"logoutBtn\" was not injected: check your FXML file 'report.fxml'.";
//        assert reportTableView != null : "fx:id=\"reportTableView\" was not injected: check your FXML file 'report.fxml'.";
//        assert toggleButtonBranch != null : "fx:id=\"toggleButtonBranch\" was not injected: check your FXML file 'report.fxml'.";
//        assert toggleButtonReports != null : "fx:id=\"toggleButtonReports\" was not injected: check your FXML file 'report.fxml'.";
//
//        EventBus.getDefault().register(this);
//        SimpleClient.getClient().getBranchList(); // Request branch list from server
//
//        // Initial visibility setup
////        updateReportsButtonVisibility();
//
//        // This section display the image of mamasKitchen
//        String imagePath = "il/cshaifasweng/OCSFMediatorExample/client/mamasKitchen.jpg";
//        Image image = new Image(imagePath);
//        MOMSImage.setImage(image);
//
//        // Check if the user is logged in (activeUser is not null)
//        if (SimpleClient.getClient().getActiveUser() != null) {
//            // If logged in, show logout button and hide login button
//            logoutBtn.setVisible(true);
//            loginBtn.setVisible(false);
//            // Check if the user is a "DIETITIAN" and display the Update button if true
//            if (SimpleClient.getClient().getActiveUser().getEmployeeType() == EmployeeType.DIETITIAN) {
//                System.out.println("Active User: " + SimpleClient.getClient().getActiveUser().getUsername());
//                UpdateMenuBtn.setVisible(true);  // Show Update button if user is a DIETITIAN
//            } else {
//                UpdateMenuBtn.setVisible(false);  // Hide Update button if user is not a DIETITIAN
//            }
//        } else {
//            // If not logged in, show login button and hide logout button
//            logoutBtn.setVisible(false);
//            loginBtn.setVisible(true);
//            UpdateMenuBtn.setVisible(false); // Hide Update button if not logged in
//        }
//
//
//        try {
//            SimpleClient.getClient().sendToServer("add client");
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        // Set the button action here
//        toggleButtonBranch.setOnAction(e -> {
//            System.out.println("Button clicked - showing popup");
//            GetBranchListPopup();
//        });
//
//    }
//
//
//
//
//
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
//
//    @FXML
//    public void getPopup(ActionEvent actionEvent) {
//        System.out.println("getPopup");
//        GetBranchListPopup();
//    }
//
//    // Handle the branch selected from the list
//    @Subscribe
//    public void onBranchSelectedEvent(BranchSelectedEvent event) {
//        System.out.println("Branch selected: " + event.getBranch().getName());
//        Branch branch = event.getBranch();
//        if (branch == null) {
//            System.out.println("branch is null");
//        }
//        openBranchPage(branch);
//    }
//
//    //open selected branch page
//    private void openBranchPage(Branch branch) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("Branch.fxml"));
//            Parent branchPageRoot = loader.load();
//            // Get the controller and pass the branch
//            BranchPageBoundary controller = loader.getController();
//            controller.setBranch(branch);
//            if (controller.branchIsSet) {
//                System.out.println("branch is already set");
//            }
//            while (!controller.branchIsSet) {
//                System.out.println("Waiting for branch to be set");
//            }
//            App.setContent(branchPageRoot);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //handle branch list sent
//    @Subscribe
//    public void onBranchListSentEvent(BranchListSentEvent event) {
//        synchronized (lock) {
//            this.branchList = event.branches;
//            this.branchListInit = true;
//            System.out.println("onBranchesSentEvent");
//            lock.notifyAll(); // Notify waiting threads that branches are initialized
//        }
//    }
//}
//
////
////private TableColumn<ReportDetail, String> createColumn(String title, String propertyName) {
////    TableColumn<ReportDetail, String> column = new TableColumn<>(title);
////    column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
////    return column;
////}
////
////
////
////
////private void populateBarChart() {
////    // Populate the bar chart with complaint data
////}
////
////
////@Subscribe
////public void onLoginSuccess(UserLoginSuccessEvent event) {
////    // Assuming that the role is part of the authorization or similar mapping
////    EmployeeType userRole = SessionManager.getInstance().getEmployeeType(); // Implement this method in SessionManager
////    toggleButtonReports.setVisible(userRole == EmployeeType.COMPANY_MANAGER);
////}
////
////private void updateReportsButtonVisibility() {
////    ActiveUser currentUser = SimpleClient.getClient().getActiveUser();
////    if (currentUser != null && currentUser.getEmployeeType() == EmployeeType.COMPANY_MANAGER) {
////        toggleButtonReports.setVisible(true);
////    } else {
////        toggleButtonReports.setVisible(false);
////    }
////}
////
////public void displayReport(String reportType) {
////    switch (reportType) {
////        case "Reservations":
////        case "Deliveries":
////            complaintsChart.setVisible(false);
////            reportTableView.setVisible(true);
////            configureTableForReport(reportType);
////            break;
////        case "Complaints":
////            reportTableView.setVisible(false);
////            complaintsChart.setVisible(true);
////            populateBarChart();  // Assume this method populates the bar chart with data
////            break;
////    }
////}
////
////private void configureTableForReport(String reportType) {
////    reportTableView.getColumns().clear();
////    if (reportType.equals("Reservations")) {
////        reportTableView.getColumns().addAll(
////                createColumn("Full Name", "fullName"),
////                createColumn("Number of Guests", "numOfGuests"),
////                createColumn("Date", "date"),
////                createColumn("Hours", "hours"),
////                createColumn("In/Out", "inOrOut")
////        );
////    } else if (reportType.equals("Deliveries")) {
////        reportTableView.getColumns().addAll(
////                createColumn("Full Name", "fullName"),
////                createColumn("Date of Delivery", "deliveryDate"),
////                createColumn("Dishes", "dishes"),
////                createColumn("Price", "price")
////        );
////    }
////    // populate the table with data
////}
////
