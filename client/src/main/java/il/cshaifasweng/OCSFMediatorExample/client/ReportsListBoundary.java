package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

public class ReportsListBoundary {

    @FXML
    private ListView<String> reportListView;

    @FXML
    private TableView<ReportDetail> reportDetailsTable;  // Assuming a type for details

    @FXML
    public void initialize() {
//        reportListView.setItems(FXCollections.observableArrayList());
//        // Handle selection of a report
//        reportListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
//            if (newValue != null) {
//                System.out.println("Selected Report: " + newValue);
//                configureTableForReport(newValue);
//                reportListView.getScene().getWindow().hide(); // Optionally close popup
//            }
//        });
        reportListView.setItems(FXCollections.observableArrayList("Reservations", "Deliveries", "Complaints"));
        reportListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
        });
    }
}


