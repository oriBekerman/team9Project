package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;


import java.util.ArrayList;
import java.util.List;

public class ReportsListBoundary {
    @FXML
    private ListView<String> reportListView;
    @FXML
    private TableView<ReportDetail> reportDetailsTable;  // Assuming a type for details

    private List<Report> reports;
    private PrimaryBoundary primaryController; // Reference to PrimaryController

    @FXML
    public void initialize() {
        reportListView.setItems(FXCollections.observableArrayList());

        // Handle selection of a report
        reportListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Selected Report: " + newValue);
                configureTableForReport(newValue);
                reportListView.getScene().getWindow().hide(); // Optionally close popup
            }
        });
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
        List<String> reportNames = new ArrayList<>();
        for (Report report : reports) {
            reportNames.add(report.getName());
        }
        reportListView.setItems(FXCollections.observableArrayList(reportNames));
    }

    private void configureTableForReport(String reportType) {
        reportDetailsTable.getColumns().clear(); // Clear existing columns
        // Dynamically add columns based on the type of report
        if (reportType.equals("Reservations")) {
            reportDetailsTable.getColumns().addAll(
                    createColumn("Full Name", "fullName"),
                    createColumn("Number of Guests", "numOfGuests"),
                    createColumn("Date", "date"),
                    createColumn("Hours", "hours"),
                    createColumn("In/Out", "inOrOut")
            );
        } else if (reportType.equals("Deliveries")) {
            reportDetailsTable.getColumns().addAll(
                    createColumn("Full Name", "fullName"),
                    createColumn("Date of Delivery", "deliveryDate"),
                    createColumn("Dishes", "dishes"),
                    createColumn("Price", "price")
            );
        }
        // Additional configurations for other types of reports
    }


    private TableColumn<ReportDetail, String> createColumn(String title, String propertyName) {
        TableColumn<ReportDetail, String> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        return column;
    }


    // Set reference to PrimaryController
    public void setPrimaryController(PrimaryBoundary primaryController) {
        this.primaryController = primaryController;
    }
}
