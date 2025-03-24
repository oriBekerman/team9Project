package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.ResInfo;
import il.cshaifasweng.OCSFMediatorExample.client.App;
import il.cshaifasweng.OCSFMediatorExample.client.SimpleClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

public class CancelReservationChooseController {

    @FXML private TableView<ResInfo> reservationTable;
    @FXML private TableColumn<ResInfo, Integer> idCol;
    @FXML private TableColumn<ResInfo, String> nameCol;
    @FXML private TableColumn<ResInfo, LocalTime> hourCol;
    @FXML private TableColumn<ResInfo, Integer> guestsCol;
    @FXML private TableColumn<ResInfo, Enum> statusCol;

    private ResInfo selected;

    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);

        idCol.setCellValueFactory(new PropertyValueFactory<>("resID"));
        nameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCustomer().getName()));
        hourCol.setCellValueFactory(new PropertyValueFactory<>("hours"));
        guestsCol.setCellValueFactory(new PropertyValueFactory<>("numOfGuests"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        try {
            String email = State.getEmail();
            SimpleClient.getClient().sendToServer(
                    new il.cshaifasweng.OCSFMediatorExample.entities.Message("get_reservations_by_email", email)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onReceiveReservations(il.cshaifasweng.OCSFMediatorExample.entities.Message message) {
        if (!message.getAction().equals("return_reservations")) return;

        List<ResInfo> reservations = (List<ResInfo>) message.getObject();
        Platform.runLater(() -> reservationTable.getItems().setAll(reservations));
    }

    @FXML
    private void handleCancelReservation() {
        selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a reservation.").show();
            return;
        }

        LocalTime now = LocalTime.now();
        long minutes = Duration.between(now, selected.getHours()).toMinutes();
        boolean withPenalty = minutes < 60;

        int penalty = withPenalty ? selected.getNumOfGuests() * 10 : 0;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                withPenalty ? "Cancellation fee is ₪" + penalty + ". Proceed?" : "Cancel without fee?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    SimpleClient.getClient().sendToServer(
                            new il.cshaifasweng.OCSFMediatorExample.entities.Message("cancel_reservation", selected.getResID())
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Subscribe
    public void onCancelReservationSuccess(il.cshaifasweng.OCSFMediatorExample.entities.Message msg) {
        if (msg.getAction().equals("cancel_reservation_success")) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Reservation cancelled successfully.");
                alert.showAndWait();
                App.switchScreen("Home Page");
            });
        } else if (msg.getAction().equals("cancel_reservation_failed")) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Error cancelling reservation.");
                alert.showAndWait();
            });
        }
    }
}