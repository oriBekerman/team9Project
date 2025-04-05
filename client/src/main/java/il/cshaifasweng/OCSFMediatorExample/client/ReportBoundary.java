package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchSelectedEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.ReportReceivedEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import il.cshaifasweng.OCSFMediatorExample.entities.Complaint;
import il.cshaifasweng.OCSFMediatorExample.entities.Delivery;
import il.cshaifasweng.OCSFMediatorExample.entities.ResInfo;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class ReportBoundary {
    private Branch currentBranch;  // Maintain a current branch state
    private SideBarBranchBoundary sidebarController;
    private boolean registered = false;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BackToHPbtn;

    @FXML
    private Label BranchName;

    @FXML
    private Label ReportType;

    @FXML
    private AnchorPane sideBarBranchPlace;

    @FXML
    private BarChart<String, Number> complaintsChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private TableView<ReportDetail> reportTableView;



    @FXML
    void BackToHP(ActionEvent event) {
        System.out.println("[ReportBoundary] Back to Home Page clicked");
        cleanup();  // explicitly unsubscribe from EventBus
        switchScreen("Home Page");
    }

    @FXML
    void initialize() {
        assert BackToHPbtn != null : "fx:id=\"BackToHPbtn\" was not injected: check your FXML file 'report.fxml'.";
        assert BranchName != null : "fx:id=\"BranchName\" was not injected: check your FXML file 'report.fxml'.";
        assert ReportType != null : "fx:id=\"ReportType\" was not injected: check your FXML file 'report.fxml'.";
        assert complaintsChart != null : "fx:id=\"complaintsChart\" was not injected: check your FXML file 'report.fxml'.";
        assert reportTableView != null : "fx:id=\"reportTableView\" was not injected: check your FXML file 'report.fxml'.";
        assert sideBarBranchPlace != null : "fx:id=\"sideBarBranchPlace\" was not injected: check your FXML file 'report.fxml'.";

        if (!registered) {
            EventBus.getDefault().register(this);
            registered = true;
            SimpleClient.getClient().getBranchList();
        }

        complaintsChart.setVisible(false);
        reportTableView.setVisible(false);

        xAxis.setCategories(FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        ));

    }

    // Method to set the branch data explicitly after initialization
    public void setReportTitle(String reportType) {
        ReportType.setText(reportType + " Report");
        BranchName.setText(this.currentBranch.getName() + " Branch");
    }

    @Subscribe
    public void onBranchSelectedEvent(BranchSelectedEvent event) {
        this.currentBranch = event.getBranch();
        System.out.println("[ReportBoundary - onBranchSelectedEvent] Branch selected - Current branch set to : " + currentBranch.getName() +
                " (ID=" + currentBranch.getId() + ")");
    }


    public void requestAndDisplayComplaintsData() {
        String branchName = getCurrentBranchName();
        if (branchName == null) {
            System.out.println("[ReportBoundary] ERROR: Cannot request complaints data, branchName is null.");
            return;
        }
        System.out.println("[ReportBoundary] Requesting complaints data for: " + branchName);
        try {
            SimpleClient.getClient().requestComplaintsReport(branchName);
        } catch (IOException e) {
            System.out.println("[ReportBoundary] IOException: " + e.getMessage());
        }
    }

    private void updateComplaintsChart(List<ReportDetail> complaintDetails) {
        Map<String, Integer> monthlyCounts = new LinkedHashMap<>();

        // Initialize with proper-cased month names
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        for (String month : months) {
            monthlyCounts.put(month, 0);
        }

        // Count complaints with normalization
        for (ReportDetail detail : complaintDetails) {
            // Normalize: e.g., "MARCH" -> "March"
            String raw = detail.getComplaintMonth();
            if(raw != null && !raw.isEmpty()){
                String normalized = raw.substring(0, 1).toUpperCase() + raw.substring(1).toLowerCase();
                // Use normalized string to merge counts
                monthlyCounts.merge(normalized, 1, Integer::sum);
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Complaints");

        // Add all months (they will appear in the correct order from the array)
        for (String month : months) {
            series.getData().add(new XYChart.Data<>(month, monthlyCounts.get(month)));
        }

        complaintsChart.getData().clear();
        complaintsChart.getData().add(series);

        complaintsChart.getYAxis().setAutoRanging(false);
        ((javafx.scene.chart.NumberAxis) complaintsChart.getYAxis()).setLowerBound(0);
        ((javafx.scene.chart.NumberAxis) complaintsChart.getYAxis()).setUpperBound(10);
        ((javafx.scene.chart.NumberAxis) complaintsChart.getYAxis()).setTickUnit(1);
    }


    private TableColumn<ReportDetail, String> createColumn(String title, String propertyName) {
        TableColumn<ReportDetail, String> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        return column;
    }

    public void displayReport(String reportType) {
        if (currentBranch == null) {
            System.out.println("[ReportBoundary] ERROR: No branch set. Cannot display report.");
            return;
        }
        System.out.println("[ReportBoundary] Displaying report: " + reportType + " for branch: " + currentBranch.getName());

        switch (reportType) {
            case "Reservations":
            case "Deliveries":
                complaintsChart.setVisible(false);
                reportTableView.setVisible(true);
                requestAndDisplayReportData(reportType);
                break;
            case "Complaints":
                reportTableView.setVisible(false);
                complaintsChart.setVisible(true);
                requestAndDisplayComplaintsData();
                break;
        }
    }


    private void configureTableForReport(String reportType) {
        reportTableView.getColumns().clear();
        if (reportType.equals("Reservations")) {
            reportTableView.getColumns().addAll(
                    createColumn("Full Name", "fullNameRES"),
                    createColumn("Number of Guests", "numOfGuests"),
                    createColumn("Reservation Date", "reservationDate"),
                    createColumn("Hours", "hours"),
                    createColumn("In/Out", "inOrOut")
            );
        } else if (reportType.equals("Deliveries")) {
            reportTableView.getColumns().addAll(
                    createColumn("Full Name", "fullNameDelivery"),
//                    createColumn("Date of Delivery", "deliveryDate"),
                    createColumn("Ordered Items", "orderedItems"),
                    createColumn("Price", "price"),
                    createColumn("Delivery Method", "deliveryMethod")
            );
        }
        reportTableView.refresh(); // Ensure refresh

    }

    private String getCurrentBranchName() {
        if (currentBranch == null) {
            System.out.println("[ReportBoundary] ERROR: currentBranch is null in getCurrentBranchName()");
            return null;
        }
        return currentBranch.getName();
    }



    private void requestAndDisplayReportData(String reportType) {
        String branchName = getCurrentBranchName();
        if (branchName == null) {
            System.out.println("No branch selected yet!");
            return;
        }
        try {
            if ("Reservations".equals(reportType)) {
                SimpleClient.getClient().requestReservationsReport(branchName);
            } else if ("Deliveries".equals(reportType)) {
                SimpleClient.getClient().requestDeliveriesReport(branchName);
            }
        } catch (IOException e) {
            System.out.println("Error requesting report data: " + e.getMessage());
        }
    }


    @Subscribe
    public void onReportDataReceived(ReportReceivedEvent event) {
        Platform.runLater(() -> {
            System.out.println("[ReportBoundary - onReportDataReceived] Event Received, event type: " + event.getReportType());
            List<ReportDetail> details = new ArrayList<>();

            if ("Reservations".equals(event.getReportType())) {
                List<ResInfo> reservations = (List<ResInfo>) event.getReportData();
                reservations.forEach(res -> {
                    System.out.println("Processing reservation: " + res);
                    ReportDetail detail = new ReportDetail(res);
                    details.add(detail);
                });
                configureTableForReport("Reservations");
                reportTableView.setItems(FXCollections.observableArrayList(details));

            } else if ("Deliveries".equals(event.getReportType())) {
                List<Delivery> deliveries = (List<Delivery>) event.getReportData();
                deliveries.forEach(delivery -> details.add(new ReportDetail(delivery)));
                configureTableForReport("Deliveries");
                reportTableView.setItems(FXCollections.observableArrayList(details));

            } else if ("Complaints".equals(event.getReportType())) {
                List<Complaint> complaints = (List<Complaint>) event.getReportData();
                complaints.forEach(complaint -> details.add(new ReportDetail(complaint)));
                updateComplaintsChart(details);
            }
        });
    }


    public void setBranch(Branch branch) {
        this.currentBranch = branch;
        System.out.println("[ReportBoundary] Branch explicitly set to: " + branch.getName());
        injectSidebarBranch();
    }

    public void cleanup() {
        EventBus.getDefault().unregister(this);
    }


    private void injectSidebarBranch() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("sideBarBranch.fxml"));
            Parent sideBarBranchRoot = loader.load();
            sidebarController = loader.getController();

            sidebarController.setBranch(this.currentBranch);  // Safe now, branch is guaranteed to be set

            sideBarBranchPlace.getChildren().clear();
            sideBarBranchPlace.getChildren().add(sideBarBranchRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

