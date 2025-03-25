package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationListController {

    @FXML private TableView<ResInfo> reservationsTable;
    @FXML private TableColumn<ResInfo, Integer> idCol;
    @FXML private TableColumn<ResInfo, String> branchCol;
    @FXML private TableColumn<ResInfo, String> timeCol;
    @FXML private TableColumn<ResInfo, Integer> guestsCol;
    @FXML private TableColumn<ResInfo, String> statusCol;

    private SimpleClient client = SimpleClient.getClient();

    @FXML
    void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("resID"));
        branchCol.setCellValueFactory(new PropertyValueFactory<>("branch"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("hours"));
        guestsCol.setCellValueFactory(new PropertyValueFactory<>("numOfGuests"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadReservations();
    }

    private void loadReservations() {
        List<ResInfo> allReservations = SimpleClient.getClient().getAllReservations();
        String email = SimpleClient.getClient().userEmail;

        List<ResInfo> filtered = allReservations.stream()
                .filter(res -> res.getCustomer() != null
                        && res.getCustomer().getEmail().equals(email)
                        && !res.getIsCancelled())
                .collect(Collectors.toList());

        reservationsTable.setItems(FXCollections.observableList(filtered));
    }


    @FXML
    void handleCancel() {
        ResInfo selected = reservationsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Please select a reservation to cancel.");
            return;
        }

        // שליחת הבקשה לשרת לביטול
        Request request = new Request(ReqCategory.CANCEL_RESERVATION, selected.getResID());
        client.sendRequest(request);

        Response<?> response = client.getResponse();

        if (response.getStatus() == Response.Status.SUCCESS) {
            showInfo(response.getMessage());
            reservationsTable.getItems().remove(selected);
        } else {
            showAlert(response.getMessage());
        }
    }

    @FXML
    void handleBack() {
        App.switchScreen("enterEmail");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
